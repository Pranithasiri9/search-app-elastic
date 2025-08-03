package com.Pranitha.elasticsearchapp.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Pranitha.elasticsearchapp.document.CourseDocument;
import com.Pranitha.elasticsearchapp.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class CourseDataLoader implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    public CourseDataLoader(CourseRepository courseRepository, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (courseRepository.count() == 0) {
            InputStream inputStream = new ClassPathResource("sample-courses.json").getInputStream();
            List<CourseDocument> courses = objectMapper.readValue(inputStream, new TypeReference<>() {});
            courseRepository.saveAll(courses);
            System.out.println("Sample data indexed into Elasticsearch.");
        } else {
            System.out.println("Courses already exist in Elasticsearch. Skipping import.");
        }
    }
}
