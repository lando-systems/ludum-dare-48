package lando.systems.ld48.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld48.levels.SpawnInteractable;
import lando.systems.ld48.screens.GameScreen;
import lando.systems.ld48.utils.Callback;

public class InteractableEntity extends GameEntity {

    public final SpawnInteractable.Type type;
    public final int id;
    public final int targetId;

    public boolean disabled = false;

    boolean completed = false;
    boolean active = false;
    Callback completionCallback;

    public InteractableEntity(GameScreen screen, float x, float y, SpawnInteractable spawner, Animation<TextureRegion> anim) {
        super(screen, anim);
        this.type = spawner.type;
        this.id = spawner.id;
        this.targetId = spawner.targetId;

        float scale = 1f;
        float width = anim.getKeyFrames()[0].getRegionWidth();
        float height = anim.getKeyFrames()[0].getRegionHeight();
        initEntity(x, y, width * scale, height * scale);

        animationPaused = true;

        completionCallback = params -> {
            if (type == SpawnInteractable.Type.lever) {
                screen.particles.interact(position.x, position.y);
            } else if (type == SpawnInteractable.Type.door) {
                screen.particles.smoke(position.x, position.y);
                removeFromScreen();
            }
            return null;
        };
    }

    public void interact() {
        if (disabled) return;
        if (completed) return;
        if (active) return;
        active = true;

        animationPaused = false;
    }

    @Override
    public void update(float dt) {
        boolean wasIncomplete = !completed;
        completed = animation.isAnimationFinished(stateTime);
        if (wasIncomplete && completed) {
            if (completionCallback != null) {
                completionCallback.call();
            }

            // trigger target, if any
            if (targetId != -1) {
                for (InteractableEntity interactable : screen.interactables) {
                    if (interactable.id == targetId) {
                        interactable.interact();

                        // uh... 'start interacting' callback I guess...
                        if (interactable.type == SpawnInteractable.Type.door) {
                            screen.particles.smoke(position.x, position.y);
                        }
                    }
                }
            }
        }
        super.update(dt);
    }


    @Override
    public void addToScreen(float x, float y) {
        screen.interactables.add(this);
        super.addToScreen(x, y);
    }

    @Override
    public void removeFromScreen() {
        screen.interactables.removeValue(this, true);
        super.removeFromScreen();
    }

}
