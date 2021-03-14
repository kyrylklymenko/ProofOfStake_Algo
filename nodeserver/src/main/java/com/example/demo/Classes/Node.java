package com.example.demo.Classes;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;

import static java.lang.Math.random;

@Entity
@Data
@EnableAutoConfiguration
@Table(name="node", schema = "public")
@AllArgsConstructor
public class Node {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "nodeId")
    private int id;
    @Column(name = "free_tokens")
    private int freeTokens;
    @Column(name = "status")
    private int status;
    @Column(name = "reputation")
    private int reputation;
    @Column(name = "bias")
    private int bias;
    @Column(name = "stake_tokens")
    private int stakeTokens;
    @Column(name = "ipv4")
    private String ipV4;


    public Node(String ip) {
        freeTokens = 100000;
        status = 0;
        reputation = 1000;
        ipV4 = ip;
        stakeTokens = 0;

        if (Math.random() < 0.3) bias = -1;
        else bias = 1;
    }

}
