package com.example.falmeida.popularmovies_proj1.data;

/**
 * Created by falmeida on 28/01/17.
 */

public enum MovieSortCriteria {
    MOST_POPULAR,
    TOP_RATED;

    @Override
    public String toString() {
        switch ( this ) {
            case MOST_POPULAR:
                return "most_popular";
            case TOP_RATED:
                return "top_rated";
        }
        throw new AssertionError("Unknown Movie sort criteria");
    }
}
