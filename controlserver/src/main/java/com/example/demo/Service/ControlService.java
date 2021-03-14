package com.example.demo.Service;

import com.example.demo.Classes.Node;
import com.example.demo.Classes.NodeRepository;
import com.example.demo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ControlService extends Thread {
    private final RestTemplate template = new RestTemplate();

    private final int maxValidators = 4, maxReputation = 1000;
    private int processState;

    @Autowired
    private NodeRepository repository;

    private int electionStatus, totalVoteStake;
    List<Integer> freeNodeIds;
    Map<Integer, Integer> validatorNodes;

    @Autowired
    public ControlService() {
        electionStatus = 0;
        totalVoteStake = 0;
        processState = 0;
        freeNodeIds = new ArrayList<>();
        validatorNodes = new HashMap<>();
    }

    public void startController(){
        processState = 1;
    }

    public String getIpById(int id){
        return repository.findById(id).get().getIpV4();
    }

    public void initiateElections(){
        if (electionStatus == 0) {
            electionStatus = 1;
            Map<Integer, Integer> candidateNodeIds = getCandidates();
            voteCandidates(candidateNodeIds);
        }

    }

    private Map<Integer, Integer> getCandidates(){

        Map<Integer, Integer> candidates = new HashMap<>();
        for (int id = 0; id < 10; id++){
            String nodeEndpoint = "http://" + getIpById(id) + ":8000/election_initiate";

            ElectionInitiateResponse response = template.exchange(nodeEndpoint, HttpMethod.POST, null, ElectionInitiateResponse.class).
                    getBody();
            if (response.getCandidateStatus() == 0 ){
                freeNodeIds.add(response.getId());
            }
            else {
                candidates.put(response.getId(), 0);
            }
        }
        return candidates;
    }

    private void voteCandidates(Map<Integer, Integer> candidateNodeIds) {
        List<Node> nodes = (List<Node>) repository.findAll();

        Map<Integer, Integer> candidateVotes = new HashMap<>();
        CandidatesList candidatesList = CandidatesList.builder().candidatesAmount(candidateNodeIds.size()).build();
        List<Integer> candidateIds = new ArrayList<>(candidateNodeIds.keySet());
        for (Node node: nodes){
            if (freeNodeIds.contains(node.getId())){
                String url = "http://" + getIpById(node.getId()) + ":8000/vote_candidates";
                VoteResponse response = template.exchange(url, HttpMethod.POST, new HttpEntity<>(candidatesList), VoteResponse.class).
                        getBody();
                int candidateId = candidateIds.get(response.getId());
                candidateNodeIds.put(candidateId, candidateNodeIds.get(candidateId) + response.getVoteStake());
                totalVoteStake += response.getVoteStake();
            }

        }

        List<Integer> vals = (List<Integer>) candidateNodeIds.values();
        vals.sort(Collections.reverseOrder());
        List<Integer> valStakes = vals.stream().limit(maxValidators).collect(Collectors.toList());

        for (Map.Entry<Integer, Integer> item: candidateNodeIds.entrySet()) {
            if (valStakes.contains(item.getValue())) validatorNodes.put(item.getKey(), item.getValue());
        }

    }

    public double transactionApproval(){
        double verdict = 0;
        for (Map.Entry<Integer, Integer> item: validatorNodes.entrySet()){
            Node validator = repository.findById(item.getKey()).get();
            String validatorUrl = "http://" + getIpById(validator.getId()) + ":8000/approve_transaction";
            ResponseEntity<Integer> response = template.exchange(validatorUrl, HttpMethod.POST, null, Integer.class);
            double weight = (item.getValue() / totalVoteStake) * (validator.getReputation()/maxReputation);
            verdict += weight * response.getBody();
        }
        return verdict;
    }

    public void pingTransaction(){
        int node = (int) (Math.random()*freeNodeIds.size());

        int nodeId = freeNodeIds.get(node);

        String ip = getIpById(nodeId);
        String endPoint = "http://" + ip + ":8000/commit_transaction";
        List<Integer> freeNodesToSend = new ArrayList<>(freeNodeIds);
        freeNodesToSend.remove(nodeId);
        FreeNodesInfo message = FreeNodesInfo.builder().freeNodes(freeNodesToSend).build();
        TransactionResponse response = template.exchange(endPoint, HttpMethod.POST, new HttpEntity<>(message), TransactionResponse.class).getBody();

        if (transactionApproval() > 0){
            int recieverId = response.getRecieverId();
            String recieverEndpoint = "http://" + getIpById(response.getRecieverId()) + ":8000/add_tokens";
            System.out.println((String) template.exchange(recieverEndpoint, HttpMethod.POST, new HttpEntity<>(response.getTransactionAmount()), String.class).getBody());

        } else System.out.println("Transaction was decided to be aborted :(");

    }

    @Override
    public void run() {
        while (true) {
            if (processState == 1) {
                //initiateElections();
                pingTransaction();
                try {
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }


}
