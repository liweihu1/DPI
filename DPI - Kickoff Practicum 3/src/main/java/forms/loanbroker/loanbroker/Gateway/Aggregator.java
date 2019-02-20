package forms.loanbroker.loanbroker.Gateway;

import mix.model.bank.BankInterestReply;

import java.util.*;

public class Aggregator {
    private Map<UUID, Integer> expectedAmountOfReplies;
    private Map<UUID, Set<BankInterestReply>> replies;

    public Aggregator(){
        expectedAmountOfReplies = new HashMap<>();
        replies = new HashMap<>();
    }

    public void addNewExpectedAmount(UUID id, int amount){
        expectedAmountOfReplies.put(id, amount);
        replies.put(id, new HashSet<>());
    }

    public int getExpectedAmountForId(UUID id) {
        return expectedAmountOfReplies.get(id);
    }

    public Map<UUID, Set<BankInterestReply>> getReplies(){
        return replies;
    }
}
