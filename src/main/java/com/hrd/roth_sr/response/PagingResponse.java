package com.hrd.roth_sr.response;

import com.hrd.roth_sr.base.Pagination;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class PagingResponse <T>{
    private final List<T> items;
    private final Pagination pagination;
}
