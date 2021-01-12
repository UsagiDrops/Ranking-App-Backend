package se.ranking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.ranking.exception.EntityNotFoundException;
import se.ranking.model.Competition;
import se.ranking.model.User;
import se.ranking.repository.CompetitionRepository;
import se.ranking.repository.UserRepository;

import java.util.List;

@Service
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Competition findById(Long id) throws EntityNotFoundException {
        return competitionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Competition not found"));
    }

    @Override
    public Competition editIfUserDoesNotExists(User user, Competition competition) {
        boolean userExists = userExistsInCompetition(user, competition);
        if(!userExists) {
            List<User> users = addUserToCompetition(user, competition);
            competition.setUsers(users);
            this.edit(competition.getId(), competition);
        }
        return competition;
    }

    private List<User> addUserToCompetition(final User user, final Competition competition) {
        List<User> users = competition.getUsers();
        users.add(user);
        return users;
    }

    private boolean userExistsInCompetition(final User user, final Competition competition) {
        return competition.getUsers().stream()
                .anyMatch(competitionUser -> competitionUser.equals(user));
    }

    @Override
    public Competition save(Competition competition) {
        return competitionRepository.save(competition);
    }

    @Override
    public List<Competition> findAll() {
        return competitionRepository.findAll();
    }

    @Override
    public Competition edit(Long id, Competition competition) {
        Competition targetCompetition = this.findById(id);
        BeanUtils.copyProperties(competition, targetCompetition, String.valueOf(id));
        competitionRepository.save(targetCompetition);
        return competition;
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        boolean competitionExists = competitionRepository.existsById(id);
        if(competitionExists) {
            competitionRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public Competition patchCompetition(JsonPatch jsonPatch, Long id) throws EntityNotFoundException, JsonPatchException, JsonProcessingException {
        Competition competition = this.findById(id);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(competition, JsonNode.class));
        Competition patchedCompetition = objectMapper.treeToValue(patched, Competition.class);

        return competitionRepository.save(patchedCompetition);
    }
}
