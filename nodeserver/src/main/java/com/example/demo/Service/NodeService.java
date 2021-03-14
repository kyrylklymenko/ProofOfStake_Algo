package com.example.demo.Service;

import com.example.demo.Classes.Node;
import com.example.demo.Classes.NodeRepository;
import com.example.demo.dto.ElectionInitiateResponse;
import com.example.demo.dto.TransactionResponse;
import com.example.demo.dto.VoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NodeService extends Thread {

    private final String address = "http://controlserver:8083";
    private final RestTemplate template = new RestTemplate();

    private final Node node;


    @Autowired
    private NodeRepository repository;


    public NodeService() {
        this.node = new Node(IpService.getThisIp());
        repository.save(node);
    }

    public Node getNode(){
        return node;
    }


    public void addTokens(int tokens){
        node.setFreeTokens(node.getFreeTokens() + tokens);
    }

    public TransactionResponse transaction(List<Integer> nodes){
        int reciever = nodes.get((int) (Math.random()*nodes.size()));
        int transactionTokens = (int) (Math.random()*node.getFreeTokens());
        return TransactionResponse.builder().recieverId(reciever).transactionAmount(transactionTokens).build();
    }

    public int approveTransaction(){
        if (node.getStatus() == 1){
            return node.getBias();
        }
        else return 0;
    }

    public ElectionInitiateResponse elecInit(){
        int status;
        if (Math.random() < 0.6){
            node.setStatus(1);
            status = 1;
        }
        else{
            node.setStatus(0);
            status = 0;
        }
        return ElectionInitiateResponse.builder().
                candidateStatus(status).
                id(node.getId()).build();
    }

    public VoteResponse voteCandidates(int num) {
        int voteStake = (int) (Math.random()*node.getFreeTokens()*0.5);
        node.setFreeTokens(node.getFreeTokens() - voteStake);
        node.setStakeTokens(voteStake);
        int candidate = (int) (Math.random()*num);
        return VoteResponse.builder().id(candidate).voteStake(voteStake).build();
    }



    @Override
    public void run() {
        try {
            //makeTransaction();
            //makeElections(node, validatorsIdList);
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
