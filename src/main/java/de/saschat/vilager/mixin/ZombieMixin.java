package de.saschat.vilager.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public class ZombieMixin extends Monster {

    private ZombieMixin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Inject(at=@At("HEAD"), method = "killed", cancellable = true)
    public void killed(ServerLevel p_34281_, LivingEntity p_34282_, CallbackInfo ci) {
        if (p_34282_ instanceof Villager && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(p_34282_, EntityType.ZOMBIE_VILLAGER, (timer) -> {})) {
            Villager villager = (Villager) p_34282_;
            ZombieVillager zombievillager = villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
            zombievillager.finalizeSpawn(p_34281_, p_34281_.getCurrentDifficultyAt(zombievillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), (CompoundTag) null);
            zombievillager.setVillagerData(villager.getVillagerData());
            zombievillager.setGossips(villager.getGossips().store(NbtOps.INSTANCE).getValue());
            zombievillager.setTradeOffers(villager.getOffers().createTag());
            zombievillager.setVillagerXp(villager.getVillagerXp());
            net.minecraftforge.event.ForgeEventFactory.onLivingConvert(p_34282_, zombievillager);
            if (!this.isSilent()) {
                p_34281_.levelEvent((Player) null, 1026, this.blockPosition(), 0);
            }
        }
        ci.cancel();
    }
}
