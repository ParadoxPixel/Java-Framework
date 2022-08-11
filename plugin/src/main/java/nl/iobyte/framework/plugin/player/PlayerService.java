package nl.iobyte.framework.plugin.player;

import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.structures.doublemap.DoubleMap;
import nl.iobyte.framework.structures.reflected.ReflectedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class PlayerService implements Service {

    private final DoubleMap<UUID, String, IPlayer> players = new DoubleMap<>(
            ReflectedMap.getMapSupplier(ConcurrentHashMap.class)
    );

    /**
     * Get amount of registered players
     *
     * @return amount
     */
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * Get a list of all registered players
     *
     * @return list of players
     */
    public List<IPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    /**
     * Get players matching predicate
     *
     * @param predicate to match
     * @return list of players matching predicate
     */
    public List<IPlayer> getPlayers(Predicate<IPlayer> predicate) {
        List<IPlayer> list = new ArrayList<>();
        for(IPlayer player : players.values())
            if(predicate.test(player))
                list.add(player);

        return list;
    }

    /**
     * Get player by id
     *
     * @param id of player
     * @return player instance
     */
    public Optional<IPlayer> getById(UUID id) {
        return players.getLeft(id);
    }

    /**
     * Get player by id
     *
     * @param name of player
     * @return player instance
     */
    public Optional<IPlayer> getByName(String name) {
        return players.getRight(name.toLowerCase());
    }

    /**
     * Get first player matching predicate
     *
     * @param predicate to match
     * @return player instance
     */
    public Optional<IPlayer> getByPredicate(Predicate<IPlayer> predicate) {
        for(IPlayer player : players.values())
            if(predicate.test(player))
                return Optional.of(player);

        return Optional.empty();
    }

    /**
     * Register player in service
     *
     * @param player instance
     */
    public void register(IPlayer player) {
        players.put(player.getId(), player.getName().toLowerCase(), player);
    }

    /**
     * Remove player by id and name
     *
     * @param id   of player
     * @param name of player
     * @return player instance
     */
    public Optional<IPlayer> remove(UUID id, String name) {
        return Optional.ofNullable(players.removeEntry(id, name.toLowerCase()));
    }

}
