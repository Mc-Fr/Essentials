package net.mcfr.listeners;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.chat.MessageData;
import net.mcfr.death.CareService;
import net.mcfr.expedition.ExpeditionService;
import net.mcfr.roleplay.Skill;
import net.mcfr.utils.McFrPlayer;

public class PlayerListener {
  private final long LAST_BREATH_INVICIBILITY = 2000;
  private final long LAST_BREATH_DELAY = 15000;
  
  @Listener
  public void onPlayerMove(MoveEntityEvent e, @First Player p) {
    if (p != null) {
      Optional<ExpeditionService> optExpeditionService = Sponge.getServiceManager().provide(ExpeditionService.class);
      if (optExpeditionService.isPresent()) {
        optExpeditionService.get().actualizePlayerState(p);
      }
      Optional<CareService> optCareService = Sponge.getServiceManager().provide(CareService.class);
      if (optCareService.isPresent()) {
        optCareService.get().actualizePlayerState(p);
      }
    }
  }
  
  @Listener
  public void onDamageEntity(DamageEntityEvent e) {
    if (e.getTargetEntity() instanceof Player) {
      Player player = (Player) e.getTargetEntity();
      
      if (McFrPlayer.getMcFrPlayer(player).isGod()) {
        e.setCancelled(true);
        return;
      }
      
      double health = player.health().get();
      double damage = e.getOriginalFinalDamage();

      if (damage >= health) {
        long lastBreathTime = Calendar.getInstance().getTime().getTime() - McFrPlayer.getMcFrPlayer(player).getLastBreathTime();

        if (lastBreathTime > this.LAST_BREATH_DELAY) {
          player.damage(health - 0.5D, DamageSources.GENERIC);
          e.setCancelled(true);
          McFrPlayer.getMcFrPlayer(player).updateLastBreathTime();

          // #f:0
          PotionEffectData effects = player.getOrCreate(PotionEffectData.class).get();
          effects.addElement(PotionEffect.builder()
              .potionType(PotionEffectTypes.SLOWNESS)
              .duration(300)
              .amplifier(3)
              .particles(false)
              .build());
          player.offer(effects);
          // #f:1

          player.sendMessage(Text.of(TextColors.DARK_RED, "Vous arrivez à votre dernier souffle. Encore un peu et vous mourrez."));

        } else if (lastBreathTime < this.LAST_BREATH_INVICIBILITY) {
          player.damage(health - 0.5D, DamageSources.GENERIC);
          e.setCancelled(true);
        }
      }

    }
  }
  
  @Listener
  public void onPlayerDeath(DestructEntityEvent.Death e) {
    if (e.getTargetEntity() instanceof Player) {
      Player player = (Player) e.getTargetEntity();
      Optional<CareService> optCareService = Sponge.getServiceManager().provide(CareService.class);
      if (optCareService.isPresent()) {
        optCareService.get().computeDeath(player);
      }
    }
  }
  
  @Listener
  public void onPlayerRespawn(RespawnPlayerEvent e) {
    Optional<CareService> optCareService = Sponge.getServiceManager().provide(CareService.class);
    if (optCareService.isPresent()) {
      optCareService.get().respawnPlayer(e);
    }
  }
  
  @Listener
  public void onPlayerRightClick(InteractEntityEvent.Secondary e, @First Player player) {
    Entity target = e.getTargetEntity();
    if (target instanceof Player) {
      if (Calendar.getInstance().getTime().getTime() - McFrPlayer.getMcFrPlayer(player).getReadDescriptionTime() > 100) {
        McFrPlayer.getMcFrPlayer(player).updateReadDescriptionTime();
        McFrPlayer otherPlayer = McFrPlayer.getMcFrPlayer((Player) e.getTargetEntity());
        player.sendMessage(Text.of(TextColors.DARK_GREEN, "* " + otherPlayer.getName() + " * " + otherPlayer.getDescription() + " *"));
      }
    }
  }
  
  /**
   * Déclenché quand un item est looté depuis un bloc cassé ou une entité tuée
   */
  @Listener
  public void onLootItem(DropItemEvent.Destruct e) {
    boolean mustLoot = true;

    Optional<EntityDamageSource> optDamageSource = e.getCause().first(EntityDamageSource.class);

    if (optDamageSource.isPresent()) {
      mustLoot = false;
      Entity source = optDamageSource.get().getSource();

      if (source instanceof Player) {
        McFrPlayer player = McFrPlayer.getMcFrPlayer((Player) source);
        int skillLevel = player.getSkillLevel(Skill.getSkillByName("chasse"), Optional.empty());

        if (skillLevel >= 12) {
          mustLoot = true;
        }
      } else if (source instanceof Arrow) {
        Arrow sourceArrow = (Arrow) source;
        Optional<UUID> arrowSource = ((Arrow) source).getCreator();
        if (arrowSource.isPresent()) {
          Optional<Entity> optEntity = Sponge.getServer().getWorld("world").get().getEntity(sourceArrow.getCreator().get());
          if (optEntity.isPresent() && optEntity.get() instanceof Player) {
            McFrPlayer player = McFrPlayer.getMcFrPlayer((Player) optEntity.get());
            int skillLevel = player.getSkillLevel(Skill.getSkillByName("chasse"), Optional.empty());
            
            if (skillLevel >= 12) {
              mustLoot = true;
            }
          }
        }
      }
    }

    if (!mustLoot) {
      e.setCancelled(true);
    }
  }
  
  @Listener
  public void onClientConnectionMessage (MessageChannelEvent e) {
    if (e instanceof ClientConnectionEvent) {
      e.setMessageCancelled(true);
    }
  }
  
  @Listener
  public void onMessageChannelEvent(MessageChannelEvent.Chat e, @First CommandSource sender) {
    
    if (sender instanceof Player) {
      MessageData data = new MessageData((Player) sender, e.getRawMessage().toPlain());
      if (data.checkConditions()) {
        data.send();
      }

      e.setCancelled(true);
    }
  }
}
