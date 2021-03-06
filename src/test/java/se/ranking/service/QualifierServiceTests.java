package se.ranking.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.ranking.model.*;
import se.ranking.repository.QualifierRepository;
import se.ranking.repository.UserRepository;
import se.ranking.util.TestUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

public class QualifierServiceTests {
    @InjectMocks
    private QualifierServiceImpl qualifierService;
    @Mock
    private QualifierRepository qualifierRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UtilServiceImpl utilService;

    private TestUtil testUtil;

    @BeforeEach
    public void setup() {
        qualifierService = new QualifierServiceImpl();
        MockitoAnnotations.initMocks(this);
        testUtil = new TestUtil();
    }

    @Test
    public void patchQualifier() throws JsonPatchException, IOException {
        Qualifier qualifier = testUtil.createQualifier();

        Assertions.assertEquals("qualifier", qualifier.getName());

        when(qualifierRepository.findById(1L)).thenAnswer(arguments -> Optional.of(qualifier));
        when(qualifierRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

        String json = "[{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"patchedName\"}]";

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        JsonPatch jsonPatch = JsonPatch.fromJson(jsonNode);

        Qualifier result = qualifierService.patchQualifier(jsonPatch, 1L);

        assertEquals("patchedName", result.getName());
    }

    @Test
    public void getQualifiedAndNotQualified() {
        Qualifier qualifier = testUtil.createQualifier();

        when(userRepository.findAll())
                .thenAnswer(i -> createTestUsers());

        when(utilService.convertStringToSeconds(anyString()))
                .thenCallRealMethod();

        when(utilService.stringToLocalDate(anyString()))
                .thenCallRealMethod();

        List<Set<RegisteredUser>> users = qualifierService.getQualifiedAndNotQualified(qualifier);

        assertEquals(2, users.get(0).size());
        assertEquals(4, users.get(1).size());
    }

    private List<RegisteredUser> createTestUsers() {
        Result result1 = testUtil.createCustomResult(1L, Discipline.STA, Card.WHITE, "3:45.4", "4:21.7", 55.4, "2020-04-03");
        Result result2 = testUtil.createCustomResult(2L, Discipline.STA, Card.RED, "3:45.4", "4:55.7", 64.4, "2020-04-03");
        Result result3 = testUtil.createCustomResult(3L, Discipline.FEN, Card.WHITE, "3:45.4", "4:21.7", 55.4, "2020-08-01");
        Result result4 = testUtil.createCustomResult(4L, Discipline.STA, Card.RED, "3:45.4", "2:55.7", 28.4, "2020-12-31");
        Result result5 = testUtil.createCustomResult(5L, Discipline.STA, Card.YELLOW, "3:45.4", "2:55.7", 78.4, "2020-05-01");
        Result result6 = testUtil.createCustomResult(6L, Discipline.STA, Card.YELLOW, "5:45.4", "3:55.7", 18.4, "2020-01-29");
        Result result7 = testUtil.createCustomResult(7L, Discipline.FEN, Card.RED, "5:45.4", "6:55.7", 78.4, "2020-01-25");
        Result result8 = testUtil.createCustomResult(8L, Discipline.STA, Card.WHITE, "5:45.4", "5:55.7", 18.4, "2020-07-21");
        Result result9 = testUtil.createCustomResult(9L, Discipline.STA, Card.WHITE, "5:45.4", "6:55.7", 78.4, "2019-01-25");
        Result result10 = testUtil.createCustomResult(10L, Discipline.STA, Card.WHITE, "5:45.4", "6:55.7", 78.4, "2022-01-25");

        RegisteredUser registeredUser1 = testUtil.createUser(1, Arrays.asList(result1, result2));
        RegisteredUser registeredUser2 = testUtil.createUser(2, Arrays.asList(result3, result4));
        RegisteredUser registeredUser3 = testUtil.createUser(3, Arrays.asList(result5, result6));
        RegisteredUser registeredUser4 = testUtil.createUser(4, Arrays.asList(result8, result7));
        RegisteredUser registeredUser5 = testUtil.createUser(5, Arrays.asList(result9));
        RegisteredUser registeredUser6 = testUtil.createUser(6, Arrays.asList(result9));

        result1.setUserId(1L);
        result2.setUserId(1L);
        result3.setUserId(2L);
        result4.setUserId(2L);
        result5.setUserId(3L);
        result6.setUserId(3L);
        result7.setUserId(4L);
        result8.setUserId(4L);
        result9.setUserId(5L);
        result10.setUserId(6L);

        return Arrays.asList(registeredUser1, registeredUser2, registeredUser3, registeredUser4, registeredUser5, registeredUser6);
    }
}
