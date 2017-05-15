package com.miaohu.web.tag;

import com.miaohu.domain.quesstion.QuestionRepository;
import com.miaohu.domain.tag.TagEntity;
import com.miaohu.domain.tag.TagRepository;
import com.miaohu.domain.tag.tagMap.TagMapEntity;
import com.miaohu.domain.tag.tagMap.TagMapRepository;
import com.miaohu.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Nbsaw on 17-5-5.
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagMapRepository tagMapRepository;
    @Autowired
    private QuestionRepository questionRepository;

    // 查找所有的标签
    @GetMapping(produces = "application/json;charset=UTF-8")
    public String findAll(){
        return JsonUtil.formatResult(200,"",tagRepository.findAll());
    }

    // 增加新的标签
    @PostMapping(value = "/add",produces = "application/json;charset=UTF-8")
    public String add(@RequestParam(name = "tagName") String tagName,
                      @RequestParam(name = "bio",defaultValue = "") String bio,
                      @RequestParam(name = "avatar",defaultValue = "http://7xqvgr.com1.z0.glb.clouddn.com/defaultTag.png") String avatar){
        String result = null;
        if (tagRepository.existsName(tagName)){
            result = JsonUtil.formatResult(400,"标签已存在!");
        }else{
            TagEntity tagEntity = new TagEntity();
            tagEntity.setName(tagName);
            tagEntity.setAvatar(avatar);
            tagEntity.setBio(bio);
            tagRepository.save(tagEntity);
            result = JsonUtil.formatResult(200,"创建标签成功!");
        }
        return result;
    }

    // 根据id查找
    @GetMapping(value = "/{id}",produces = "application/json;charset=UTF-8")
    public String findById(@PathVariable("id") Long id){
        List<TagMapEntity> tagMapEntities =  tagMapRepository.findAllByTagId(id);
        List result = new LinkedList();
        tagMapEntities.stream().forEach(map->{
            if (map.getType().equals("question"))
            result.add(questionRepository.findById(map.getCorrelation()));
        });
        return JsonUtil.formatResult(200,"",result);
    }

    // 根据名字查找
    @GetMapping(value = "/search/{name}",produces = "application/json;charset=UTF-8")
    public String findByNameLike(@PathVariable("name") String name){
        return JsonUtil.formatResult(200,"",tagRepository.findAllByNameLike(name));
    }
}