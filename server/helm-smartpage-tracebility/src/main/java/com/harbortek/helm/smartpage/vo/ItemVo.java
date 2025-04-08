package com.harbortek.helm.smartpage.vo;

import com.harbortek.helm.tracker.vo.items.TrackerItemVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@FieldNameConstants
public class ItemVo extends TrackerItemVo {
    List<ItemVo> children = new ArrayList<>();
}
