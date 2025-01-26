package com.jonnie.elearning.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sectionId;
    private String sectionName;
    private String secctionTitle;
    private String pdfUrl;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;
}
