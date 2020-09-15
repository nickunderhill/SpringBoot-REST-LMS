package com.softserve.marathon.model;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id", scope= Course.class)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private boolean closed;

    @NotNull
    @Length(min = 3, max = 20)
    private String title;

    @OneToMany(mappedBy = "course")
    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private List<Sprint> sprints = new ArrayList<>();

    @ManyToMany(cascade={CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "course_user",
            joinColumns = @JoinColumn(name = "id_course"),
            inverseJoinColumns = @JoinColumn(name = "id_user"))
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    private List<User> users = new ArrayList<>();

    public Course() {
    }

    public Course(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Course(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (closed != course.closed) return false;
        return title != null ? title.equals(course.title) : course.title == null;
    }

    @Override
    public int hashCode() {
        int result = (closed ? 1 : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
