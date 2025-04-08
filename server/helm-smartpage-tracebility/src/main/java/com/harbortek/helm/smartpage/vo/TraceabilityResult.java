package com.harbortek.helm.smartpage.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TraceabilityResult {
    List<String> tableColumns = new ArrayList<>();
    List<ItemVo> data = new ArrayList<>();
    Integer total = 0;
}
