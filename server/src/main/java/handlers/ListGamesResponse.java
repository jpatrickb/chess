package handlers;

import model.GameResponseData;

import java.util.Collection;

/**
 * Handles responses to listing the games by accepting a Collection of games as an attribute
 * @param games the Collection of GameResponseData to be returned
 */
public record ListGamesResponse(Collection<GameResponseData> games) {
}
