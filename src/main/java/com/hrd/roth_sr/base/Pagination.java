package com.hrd.roth_sr.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
    private int totalElements;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
