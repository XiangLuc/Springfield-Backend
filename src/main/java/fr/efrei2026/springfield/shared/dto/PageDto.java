package fr.efrei2026.springfield.shared.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageDto<T> {

    private final List<T> contenu;
    private final int page;
    private final int taille;
    private final long totalElements;
    private final int totalPages;

    public PageDto(Page<T> page) {
        this.contenu = page.getContent();
        this.page = page.getNumber();
        this.taille = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }

    public List<T> getContenu() {
        return contenu;
    }

    public int getPage() {
        return page;
    }

    public int getTaille() {
        return taille;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
