package com.sparta.neonaduriback.post.days.model;

import com.sparta.neonaduriback.common.model.Timestamped;
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
public class Days extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dayId;

    @Column(nullable = false)
    private int dateNumber;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "DAYS_ID")
    private List<Places> places = new ArrayList<>();

    public Days(int dateNumber, List<Places> placesList){
        this.dateNumber=dateNumber;
        this.places=placesList;
    }
}