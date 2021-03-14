package com.example.demo.Api;

import com.example.demo.Classes.Node;
import com.example.demo.Service.NodeService;
import com.example.demo.Service.IpService;
import com.example.demo.dto.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/node")
public class NodeController {
    private Set<Integer> validatorsIdList;
    private Node node;

    @Autowired
    private NodeService service;

    @Autowired
    public NodeController() {
        this.validatorsIdList = new HashSet<>();
        NodeService electionService = new NodeService();
        IpService ipService = new IpService();
        electionService.start();
        ipService.start();
    }

    @PostMapping("/election_initiate")
    public ResponseEntity<ElectionInitiateResponse> electionInitiateResponse(){
        return ResponseEntity.ok(service.elecInit());
    }

    @PostMapping("/vote_candidates")
    public ResponseEntity<VoteResponse> voteCandidates(@RequestBody CandidatesList candidates){
        return ResponseEntity.ok(service.voteCandidates(candidates.getCandidatesAmount()));
    }

    @PostMapping("/approve_transaction")
    public ResponseEntity<Integer> transactionApproval(){
        return ResponseEntity.ok(service.approveTransaction());
    }

    @PostMapping("/commit_transaction")
    public ResponseEntity<TransactionResponse> transaction(@RequestBody FreeNodesInfo nodes){
        return ResponseEntity.ok(service.transaction(nodes.getFreeNodes()));
    }



    @PatchMapping("/add_tokens")
    public ResponseEntity<String> addTokens(@RequestParam int tokens){
        service.addTokens(tokens);
        return ResponseEntity.ok("Node " + service.getNode().getId() + " recieved " + tokens + " tokens");
    }


}
