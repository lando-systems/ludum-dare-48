package lando.systems.ld48.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld48.Game;
import lando.systems.ld48.entities.Player;
import lando.systems.ld48.levels.Level;
import lando.systems.ld48.levels.LevelDescriptor;
import lando.systems.ld48.physics.PhysicsComponent;
import lando.systems.ld48.physics.PhysicsSystem;

public class GameScreen extends BaseScreen {

    public Level level;
    public Player player;

    public PhysicsSystem physicsSystem;
    public Array<PhysicsComponent> physicsEntities;

    public GameScreen(Game game) {
        super(game);
        loadLevel(LevelDescriptor.test);
    }

    private void loadLevel(LevelDescriptor levelDescriptor) {
        this.level = new Level(levelDescriptor, this);
        this.player = new Player(this, level.getPlayerSpawn());
        this.physicsSystem = new PhysicsSystem(this);
        this.physicsEntities = new Array<>();
        this.physicsEntities.add(player);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) DebugFlags.renderFpsDebug     = !DebugFlags.renderFpsDebug;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) DebugFlags.renderLevelDebug   = !DebugFlags.renderLevelDebug;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) DebugFlags.renderPlayerDebug  = !DebugFlags.renderPlayerDebug;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) DebugFlags.renderPhysicsDebug = !DebugFlags.renderPhysicsDebug;

        player.update(dt);
        level.update(dt);
        physicsSystem.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        // draw world stuff
        batch.setProjectionMatrix(worldCamera.combined);
        {
            batch.begin();
            {
                // draw distant background
            }
            batch.end();

            level.render(Level.LayerType.background, worldCamera);
            level.render(Level.LayerType.collision, worldCamera);

            batch.begin();
            {
                player.render(batch);
                level.renderObjects(batch);
            }
            batch.end();

            level.render(Level.LayerType.foreground, worldCamera);

            batch.begin();
            {
                // draw foreground entity decorations and such
            }
            batch.end();

            batch.begin();
            {
                if (DebugFlags.renderLevelDebug) {
                    level.renderDebug(batch);
                }
                if (DebugFlags.renderPlayerDebug) {
                    player.renderDebug(batch);
                }
                if (DebugFlags.renderPhysicsDebug) {
                    physicsSystem.renderDebug(batch);
                }
            }
            batch.end();
        }

        // draw window space stuff
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            if (DebugFlags.renderFpsDebug) {
                game.assets.pixelFont16.draw(batch, " fps: " + Gdx.graphics.getFramesPerSecond(), 10f, windowCamera.viewportHeight - 10f);
            }
            // draw overlay ui stuff
        }
        batch.end();
    }

    // ------------------------------------------------------------------------
    // Implementation stuff
    // ------------------------------------------------------------------------

    static class DebugFlags {
        public static boolean renderFpsDebug = true;
        public static boolean renderLevelDebug = false;
        public static boolean renderPlayerDebug = false;
        public static boolean renderPhysicsDebug = false;
    }

}