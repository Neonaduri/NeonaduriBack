package com.sparta.neonaduriback.post.days.model;

import com.sparta.neonaduriback.post.model.Post;
import com.sparta.neonaduriback.post.places.Places;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Days {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dayId;

    @Column(nullable = false)
    private int dateNumber;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "POST_ID")
//    private Post post;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "DAYS_ID")
    private List<Places> places = new ArrayList<>();

    public Days(int dateNumber, List<Places> placesList){
        this.dateNumber=dateNumber;
        this.places=placesList;
    }
}