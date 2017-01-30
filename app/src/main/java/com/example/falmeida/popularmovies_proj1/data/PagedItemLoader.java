package com.example.falmeida.popularmovies_proj1.data;

import java.util.List;

/**
 * Created by falmeida on 28/01/17.
 */

public interface PagedItemLoader<T> {
    int firstPage();
    int lastPage();
    int maxItemsPerPage();

    int totalPages();
    int totalResults();

    List<T> getItems(int page);
}
