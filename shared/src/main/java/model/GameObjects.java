package model;

import java.util.concurrent.ConcurrentHashMap;

public record GameObjects(ConcurrentHashMap<Integer, GameData> gameObjects) {
}
