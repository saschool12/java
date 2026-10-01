package com.example;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    @Test
    void appGreetingIsPresent() {
        App app = new App();
        assertNotNull(app.getGreeting());
        assertTrue(app.getGreeting().contains("Java Q&A Quiz & Random Question Generator"));
    }

    @Test
    void questionBankLoadsQuestions() {
        App.QuestionBank bank = new App.QuestionBank();
        assertTrue(bank.getTotalCount() >= 20, "Question bank should contain at least 20 questions");
    }

    @Test
    void randomQuestionGeneratorWorks() {
        App.QuestionBank bank = new App.QuestionBank();
        App.Question q = bank.getRandomQuestion("Collections", "Medium");
        assertNotNull(q);
        assertNotNull(q.getQuestion());
        assertFalse(q.getOptions().isEmpty());
        assertTrue(q.getCorrectIndex() >= 0 && q.getCorrectIndex() < q.getOptions().size());
    }

    @Test
    void filterQuestionsByTopicAndDifficulty() {
        App.QuestionBank bank = new App.QuestionBank();
        List<App.Question> oopList = bank.filterQuestions("OOP", null);
        assertNotNull(oopList);
        assertFalse(oopList.isEmpty());

        List<App.Question> easyList = bank.filterQuestions(null, "Easy");
        assertNotNull(easyList);
        assertFalse(easyList.isEmpty());
    }

    @Test
    void verifyQuestionAnswerIntegrity() {
        App.QuestionBank bank = new App.QuestionBank();
        App.Question q1 = bank.getQuestionById(1);
        assertNotNull(q1);
        assertEquals(0, q1.getCorrectIndex());
        assertTrue(q1.getExplanation().contains("pass-by-value"));
    }
}
