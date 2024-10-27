package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import org.neo4j.driver.*;

public class App {
    private JButton numActorsButton;
    private JButton numMoviesButton;
    private JButton sameMovieButton;
    private JTextArea outputArea;
    private JLabel connectionStatusLabel;
    private JPanel mainPanel;
    private Neo4jApp neo4jApp;

    public App() {
        initializeNeo4jConnection();
        setupEventListeners();
    }

    private void initializeNeo4jConnection() {
        neo4jApp = new Neo4jApp("bolt://localhost:7687", "neo4j", "COS326Password");
        connectionStatusLabel.setText(neo4jApp.connect());
    }

    private void setupEventListeners() {
        numActorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long count = neo4jApp.countActors();
                outputArea.setText("Number of actors: " + count);
            }
        });

        numMoviesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> actors = neo4jApp.top100ActorsByMovies();
                outputArea.setText(String.join("\n", actors));
            }
        });

        sameMovieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> pairs = neo4jApp.actorsInSameMovie();
                outputArea.setText(String.join("\n", pairs));
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Neo4j Swing App");
        frame.setContentPane(new App().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

class Neo4jApp {
    private final Driver driver;

    public Neo4jApp(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password), Config.builder().build());
    }

    public void close() {
        driver.close();
    }

    public String connect() {
        try (Session session = driver.session()) {
            String greeting = session.writeTransaction(tx -> {
                Result result = tx.run("RETURN 'Connected to Neo4j' AS message");
                return result.single().get("message").asString();
            });
            return greeting;
        } catch (Exception e) {
            return "Failed to connect: " + e.getMessage();
        }
    }

    public long countActors() {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (a:Actor) RETURN count(a) AS count");
                return result.single().get("count").asLong();
            });
        }
    }

    public List<String> top100ActorsByMovies() {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                    "MATCH (a:Actor)-[:ACTED_IN]->(m:Movie) " +
                    "RETURN a.name AS actorName, count(m) AS movieCount " +
                    "ORDER BY movieCount DESC LIMIT 100"
                );
                List<String> actors = new ArrayList<>();
                while (result.hasNext()) {
                    org.neo4j.driver.Record record = result.next();
                    actors.add(record.get("actorName").asString() + ": " + record.get("movieCount").asInt());
                }
                return actors;
            });
        }
    }

    public List<String> actorsInSameMovie() {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                // Query to match actors who have acted in the same movie and group them by movie title
                Result result = tx.run(
                    "MATCH (a:Actor)-[:ACTED_IN]->(m:Movie) " +
                    "WITH m.title AS movieTitle, collect(a.name) AS actors " +
                    "WHERE size(actors) > 1 " + // Filter to include only movies with more than one actor
                    "RETURN movieTitle, actors"
                );
                
                List<String> movieActors = new ArrayList<>();
                while (result.hasNext()) {
                    org.neo4j.driver.Record record = result.next();
                    String movieTitle = record.get("movieTitle").asString();
                    List<String> actors = record.get("actors").asList(Value::asString);
                    // Format the output to show the movie title followed by the list of actors
                    movieActors.add(movieTitle + ": " + String.join(", ", actors));
                }
                return movieActors;
            });
        }
    }
}