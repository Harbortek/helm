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

import com.harbortek.helm.tracker.service.CollectionService;
import com.harbortek.helm.tracker.vo.collection.CollectionVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "/tracker/collection")
public class CollectionApi {

    @Autowired
    CollectionService collectionService;

    @Parameter(name="查询集合列表")
    @RequestMapping(value = "/listByProject", method = RequestMethod.GET)
    ResponseEntity<Page<CollectionVo>> findCollectionsByProjectId(
            @RequestParam(value = "projectId", required = false) Long projectId,
            @RequestParam(required = false,defaultValue = "") String keyword, Pageable pageable) {
        Page<CollectionVo> collections = collectionService.findCollectionsByProjectId(projectId,keyword,pageable);
        return ResponseEntity.ok(collections);
    }

    @Parameter(name="根据文档查询集合列表")
    @RequestMapping(value = "/listByDocument", method = RequestMethod.GET)
    ResponseEntity<Collection<CollectionVo>> findCollectionsByDocumentId(@RequestParam(value = "projectId") Long projectId,
                                                                     @RequestParam(value = "documentId") Long documentId) {
        Collection<CollectionVo> collections = collectionService.findCollectionsByDocumentId(projectId, documentId);
        return ResponseEntity.ok(collections);
    }

    @Parameter(name="查询一个集合")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<CollectionVo> findOneCollection(@PathVariable Long id) {
        CollectionVo collection = collectionService.findOneCollection(id);
        return ResponseEntity.ok(collection);
    }

    @Parameter(name="创建一个集合")
    @RequestMapping(value = "", method = RequestMethod.POST)
    ResponseEntity<CollectionVo> createCollection(@RequestBody CollectionVo collectionVo) {
        CollectionVo result = collectionService.createCollection(collectionVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="更新一个集合")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    ResponseEntity<CollectionVo> updateCollection(@RequestBody CollectionVo collectionVo) {
        CollectionVo result = collectionService.updateCollection(collectionVo);
        return ResponseEntity.ok(result);
    }

    @Parameter(name="删除一个集合")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.ok().build();
    }


}
