package com.miaohu.web.question;

import com.miaohu.domain.quesstion.questionComment.QuestionCommentEntity;
import com.miaohu.domain.quesstion.questionComment.QuestionCommentRepository;
import com.miaohu.domain.quesstion.QuestionEntity;
import com.miaohu.domain.quesstion.QuestionRepository;
import com.miaohu.domain.tag.TagRepository;
import com.miaohu.domain.tag.tagMap.TagMapEntity;
import com.miaohu.domain.tag.tagMap.TagMapRepository;
import com.miaohu.util.JsonUtil;
import com.miaohu.service.getUserInfo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by fz on 17-3-31.
 */
@RestController
@RequestMapping(value = "/question")
public class QuestionController {
    // 问题
    @Autowired
    private QuestionRepository questionRepository;
    // 标签
    @Autowired
    private TagRepository tagRepository;
    // 标签映射
    @Autowired
    private TagMapRepository tagMapRepository;
    // 评论
    @Autowired
    private QuestionCommentRepository questionCommentRepository;

    /**
     * 根据传过来的id获取某个问题
     *
     * @param id 文章的id
     * @return id对应的问题
     */
    // TODO 要检查是否回复过了。
    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public QuestionEntity getId(@PathVariable("id") Long id) {
        return questionRepository.findById(id);
    }

    /**
     * 验证标题是否合法
     * 有坑,问号不同算不同问题，知乎也是这样子
     * 所以我懒得改，当作是feature !!
     *
     * @param title
     * @return
     */
    @PostMapping(value = "/valid", produces = "application/json;charset=UTF-8")
    public String validTitle(@RequestParam("title") String title) {
        String result = null;
        title = title.trim();
        String last = title.substring(title.length() - 1); // 最后一个字符
        // 判断标题是否为空
        if (title.equals("")) {
            result = JsonUtil.formatResult(400, "标题不能为空");
        }
        // 51个字的标题限制
        else if (title.length() > 51) {
            result = JsonUtil.formatResult(400, "标题太长");
        }
        // 末尾问号判断
        else if (!last.equals("?") && !last.equals("？")) {
            result = JsonUtil.formatResult(400, "你还没有给问题添加问号");
        } else {
            boolean isExists = questionRepository.existsQuestion(title);
            if (isExists)
                result = JsonUtil.formatResult(400, "已经存在的问题");
            else
                result = JsonUtil.formatResult(200, "可以创建的问题");
        }
        return result;
    }

    /**
     * 获取用户发表的所有的问题
     *
     * @param session
     * @return 获取用户发表的所有的问题
     */
    @GetMapping(value = "/select", produces = "application/json;charset=UTF-8")
    public List<QuestionEntity> all(HttpSession session) {
        String userId = ((UserInfoVO) session.getAttribute("data")).getId();
        // TODO 查找问题所属的标签
        //        List<TagMapEntity> tagMapEntities = tagMapRepository.findAllByTagIdAndType(id,"question");
//        List list = new LinkedList();
//        tagMapEntities.stream().forEach(map -> {
//            System.out.println(map);
//            list.add(tagRepository.findById(map.getTagId()).getName());
//        });

        return questionRepository.findAllByUid(userId);
    }

    /**
     * 根据传过来的id删除某个问题
     *
     * @param id      问题的id
     * @param session
     * @return 返回状态, 问题删除成功或者失败
     */
    @DeleteMapping(value = "/delete/{id}", produces = "application/json;charset=UTF-8")
    public String delete(@PathVariable(value = "id") Long id, HttpSession session) {
        String userId = ((UserInfoVO) session.getAttribute("data")).getId();
        String result = null;
        if (questionRepository.deleteById(id) == 1)
            result = JsonUtil.formatResult(200, "删除问题成功");
        else
            result = JsonUtil.formatResult(400, "删除问题失败");
        return result;
    }

    /**
     * 根据id以及传过来的标题,内容修改
     * 对应的问题
     *
     * @param id      问题的id
     * @param title   问题的标题
     * @param content 问题的内容
     * @param session
     * @return 返回状态, 修改成功或者失败
     * TODO 加个问题修改理由表
     * TODO 加上是否匿名的字段
     */
    @PostMapping(value = "/modify", produces = "application/json;charset=UTF-8")
    public String modify(@RequestParam(value = "id") Long id,
                         @RequestParam(value = "title") String title,
                         @RequestParam(value = "content", defaultValue = "") String content,
                         @RequestParam(value = "anonymous",defaultValue = "false") boolean anonymous,
                         HttpSession session) {
        String uid = ((UserInfoVO) session.getAttribute("data")).getId();
        String result = null;
        if (questionRepository.updateContentByIdAndUid(id, uid, title, content) == 1)
            result = JsonUtil.formatResult(200, "问题修改成功");
        else
            result = JsonUtil.formatResult(400, "问题修改失败");
        return result;
    }

    /**
     * 发布一个新的问题,根据session
     * 里面查到的用户,先判断问题是否
     * 已经存在在数据库里面了,如果存在
     * 返回失败,不存在则发布
     *
     * @param title   问题的标题
     * @param content 问题的内容
     * @param session
     * @return 先判断问题是否已经存在在
     * 数据库里面了如果存在返回失败,不存
     * 在则发布
     */
    @PostMapping(value = "/post", produces = "application/json;charset=UTF-8")
    public String post(@RequestParam(value = "title") String title,
                       @RequestParam(value = "content") String content,
                       @RequestParam(value = "tags") Long[] tags,
                       HttpSession session) {
        QuestionEntity questionEntity = new QuestionEntity();
        boolean isExists = questionRepository.existsQuestion(content);
        title = title.trim();
        String last = title.substring(title.length() - 1); // 最后一个字符
        String result = null;
        // 判断标题是否为空
        if (title.equals("")) {
            result = JsonUtil.formatResult(400, "标题不能为空");
        }
        // 51个字的标题限制
        else if (title.length() > 51) {
            result = JsonUtil.formatResult(400, "标题太长");
        }
        // 末尾问号判断
        else if (!last.equals("?") && !last.equals("？")) {
            result = JsonUtil.formatResult(400, "你还没有给问题添加问号");
        }
        // 尝试创建
        else {
            // 判断传过来的标签是否合法
            String uid = ((UserInfoVO) session.getAttribute("data")).getId();
            for (long s : tags)
                if (!tagRepository.exists(s))
                    return JsonUtil.formatResult(400, "无效的标签");
            // 标签都合法保存下来
            for (long s : tags) {
                TagMapEntity tagMapEntity = new TagMapEntity();
                tagMapEntity.setCorrelation(questionEntity.getId());
                tagMapEntity.setTagId(s);
                tagMapEntity.setType("question");
                tagMapRepository.save(tagMapEntity);
            }
            // 保存问题
            questionEntity.setUid(uid);
            questionEntity.setTitle(title);
            questionEntity.setContent(content);
            questionRepository.save(questionEntity);
            result = JsonUtil.formatResult(200, "问题发布成功");
        }
        return result;
    }

    // 查找问题的评论
    // TODO 要做分页哦
    @GetMapping(value = "/comment/{id}", produces = "application/json;charset=UTF-8")
    public String selectById(@PathVariable("id") Long id) {
        List<QuestionCommentEntity> list = questionCommentRepository.findAllByQuestionId(id, "question");
        return JsonUtil.formatResult(200, "", list);
    }

    /**
     * 回答问题
     *
     * @param questionId
     * @param content    //     * @param session
     * @return
     */
    @PostMapping(value = "/comment/add", produces = "application/json;charset=UTF-8")
    public String comment(@RequestParam(value = "questionId") Long questionId,
                          @RequestParam(value = "content") String content,
                          HttpSession session) {
        String result = null;
        String uid = ((UserInfoVO) session.getAttribute("data")).getId();
        // 检测id是否为空
        if (questionId == null) {
            result = JsonUtil.formatResult(403, "帖子id不应该为空");
        }
        // 检测是不是已经回答过的问题
        else if (questionCommentRepository.isReply(questionId, uid)) {
            result = JsonUtil.formatResult(403, "不可以重复回答问题:(");
        }
        // 检验回复是否为空
        else if (content.trim().length() == 0) {
            result = JsonUtil.formatResult(400, "评论不能为空");
        }
        // 验证通过提交
        else {
            String userId = ((UserInfoVO) session.getAttribute("data")).getId();
            QuestionCommentEntity questionCommentEntity = new QuestionCommentEntity();
            questionCommentEntity.setQuestionId(questionId);
            questionCommentEntity.setContent(content);
            questionCommentEntity.setUid(userId);
            questionCommentRepository.save(questionCommentEntity);
            result = JsonUtil.formatResult(200, "评论成功");
        }
        return result;
    }

    // 回答删除接口
    @DeleteMapping(value = "/comment/delete", produces = "application/json;charset=UTF-8")
    public String deleteComment(
            @RequestParam(value = "questionId") Long questionId
            , HttpSession session) {
        String result = null;
        String uid = ((UserInfoVO) session.getAttribute("data")).getId();
        boolean isReply = questionCommentRepository.isReply(questionId, uid);
       if (isReply){
           boolean isDeleted = questionCommentRepository.isDeleted(questionId, uid);
           if (isDeleted) {
               result = JsonUtil.formatResult(400, "已经是删除状态了！");
           } else {
               questionCommentRepository.setDeletedTrue(questionId, uid);
               result = JsonUtil.formatResult(200, "回答已删除");
           }
       }
       else{
           result = JsonUtil.formatResult(400, "没有回答的过问题无法删除");
       }
        return result;
    }

    // 撤销删除
    @PostMapping(value = "/comment/revoke", produces = "application/json;charset=UTF-8")
    public String revokeComment(
            @RequestParam(value = "questionId") Long questionId,
            HttpSession session) {
        String result = null;
        String uid = ((UserInfoVO) session.getAttribute("data")).getId();
        boolean isReply = questionCommentRepository.isReply(questionId, uid);
        if (isReply) {
            boolean isDeleted = questionCommentRepository.isDeleted(questionId, uid);
            if (isDeleted) {
                questionCommentRepository.setDeletedFalse(questionId, uid);
                result = JsonUtil.formatResult(200, "撤销成功");
            } else {
                result = JsonUtil.formatResult(400, "已经是撤销状态了!");
            }
        }else{
            result = JsonUtil.formatResult(400, "没有回答过的问题无法撤销");
        }
        return result;
    }

    // 设置问题为匿名 / 取消匿名
    // TODO 同时还要设置回答为匿名
    @PostMapping(value = "/anonymous",produces = "application/json;charset=UTF-8")
    public String setAnonymous(
            @RequestParam(value = "questionID") Long questionId,
            HttpSession session){
        String result = null;
        String uid = ((UserInfoVO) session.getAttribute("data")).getId();
        boolean isExists = questionRepository.isExists(questionId);
        if (isExists) {
            boolean isAnonymous = questionRepository.isAnonymous(questionId);
            if (isAnonymous) {
                questionCommentRepository.setDeletedTrue(questionId, uid);
                result = JsonUtil.formatResult(200, "已经设为匿名!");
            } else {
                questionCommentRepository.setDeletedFalse(questionId, uid);
                result = JsonUtil.formatResult(400, "已经取消匿名!");
            }
        }else{
            result = JsonUtil.formatResult(400, "问题不存在");
        }
        return result;
    }

    // TODO 回答修改接口


}