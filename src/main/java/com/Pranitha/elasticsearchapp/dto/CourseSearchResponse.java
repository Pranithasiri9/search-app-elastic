package com.Pranitha.elasticsearchapp.dto;

import com.Pranitha.elasticsearchapp.document.CourseDocument;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CourseSearchResponse {

    private long total;
    private List<CourseSummary> courses;

    public CourseSearchResponse(long total, List<CourseDocument> documents) {
        this.total = total;
        this.courses = documents.stream()
                .map(CourseSummary::new)
                .collect(Collectors.toList());
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<CourseSummary> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseSummary> courses) {
        this.courses = courses;
    }

    public static class CourseSummary {
        private String id;
        private String title;
        private String category;
        private Double price;
        private String nextSessionDate;

        public CourseSummary(CourseDocument doc) {
            this.id = doc.getId();
            this.title = doc.getTitle();
            this.category = doc.getCategory();
            this.price = doc.getPrice();
            this.nextSessionDate = (doc.getNextSessionDate() != null)
                    ? doc.getNextSessionDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    : null;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public Double getPrice() {
            return price;
        }

        public String getNextSessionDate() {
            return nextSessionDate;
        }
    }
}
