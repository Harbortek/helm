/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.tracker.api;

import com.harbortek.helm.tracker.service.ViewService;
import com.harbortek.helm.tracker.vo.view.ViewVo;
import com.harbortek.helm.util.ObjectUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Controller
@Tag(name = "项目管理")
@RequestMapping(value = "/tracker/project/view")
public class ViewApi {
    @Autowired
    ViewService viewService;
    @Parameter(name="查询视图列表接口")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<List<ViewVo>> findViewsByObjectId(@RequestParam Long objectId,@RequestParam(required = false) Long userId,
                                                           @RequestParam Boolean display,@RequestParam(required = false) Boolean isMatter){
        List<ViewVo> viewVos = viewService.findByObjectId(objectId,userId,display,isMatter);
        //isMatter 1.为空时不自动创建视图 2.true 我的事项视图  3.false tracker视图
        if(viewVos.isEmpty()&& ObjectUtils.isNotEmpty(isMatter)){
            if(isMatter){
                viewService.createMattersDefaultView(objectId,userId);
            }else{
                viewService.createDefaultView(objectId);
            }
           viewVos = viewService.findByObjectId(objectId,userId,display,isMatter);
        }
        return ResponseEntity.ok(viewVos);
    }

    @Parameter(name="查询一个视图")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<ViewVo> findOneView(@PathVariable Long id){
        ViewVo viewVo = viewService.findById(id);
        return ResponseEntity.ok(viewVo);
    }
    @Parameter(name="创建一个视图")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<ViewVo> createView(@RequestBody ViewVo viewVo){
        ViewVo result = viewService.createView(viewVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="创建默认视图")
    @RequestMapping(value = "default", method = RequestMethod.POST)
    ResponseEntity<Void> createDefaultView(@RequestParam Long  trackerId){
        viewService.createDefaultView(trackerId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="创建我的事项默认视图")
    @RequestMapping(value = "matters/default", method = RequestMethod.POST)
    ResponseEntity<Void> createMattersDefaultView(@RequestParam Long  pageId,Long userId){
        viewService.createMattersDefaultView(pageId,userId);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新一个视图")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<Void> updateView(@RequestBody ViewVo viewVo){
        viewService.updateView(viewVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="更新视图顺序")
    @RequestMapping(value = "/ordinary", method = RequestMethod.PUT)
    ResponseEntity<Void> updateViewsOrdinary(@RequestBody List<ViewVo> viewVos){
        viewService.updateViewsOrdinary(viewVos);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="重命名一个视图")
    @RequestMapping(value = "/rename", method = RequestMethod.PUT)
    ResponseEntity<Void> renameView(@RequestBody ViewVo viewVo){
        viewService.renameView(viewVo);
        return ResponseEntity.ok().build();
    }

    @Parameter(name="删除一个视图")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteView(@PathVariable Long  id){
        viewService.deleteView(id);
        return ResponseEntity.ok().build();
    }
}
