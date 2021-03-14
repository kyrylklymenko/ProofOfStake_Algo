package com.example.demo.Classes;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.persistence.*;

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

}
