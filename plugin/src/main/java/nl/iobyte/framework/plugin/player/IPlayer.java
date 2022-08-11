package nl.iobyte.framework.plugin.player;

import net.kyori.adventure.text.TextComponent;

import java.util.UUID;

public interface IPlayer {

    /**
     * Get uuid of player
     *
     * @return uuid of player
     */
    UUID getId();

    /**
     * Get the name of a player
     *
     * @return name
     */
    String getName();

    /**
     * Check if player has permission
     *
     * @param permission to check for
     * @return whether player has permission or not
     */
    boolean hasPermission(String permission);

    /**
     * Set if player is allowed to have a permission
     *
     * @param permission to set
     * @param allowed    whether permission is allowed or not
     */
    void setPermission(String permission, boolean allowed);

    /**
     * Send player a message
     *
     * @param text to send
     */
    void sendMessage(TextComponent text);

}
