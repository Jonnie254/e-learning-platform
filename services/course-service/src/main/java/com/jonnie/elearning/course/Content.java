package com.jonnie.elearning.course;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String contentId;
    private String name;
    private boolean isDeleted;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<Section> sections;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
