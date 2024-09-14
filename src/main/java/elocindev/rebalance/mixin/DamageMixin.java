package elocindev.rebalance.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import elocindev.rebalance.ReBalance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

@Mixin(value = LivingEntity.class, priority = 1000)
public abstract class DamageMixin {
    
    @Shadow protected float lastDamageTaken;
    @Shadow private long lastDamageTime;
    @Shadow public abstract float getMaxHealth();
    @Shadow @Nullable public abstract LivingEntity getAttacker();
    @Shadow public abstract float getHealth();
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Inject(method = "modifyAppliedDamage", at = @At("RETURN"), cancellable = true)
    protected void rebalance$modifyAppliedDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity attacker = this.getAttacker();
        LivingEntity entity = (LivingEntity)(Object)this;

        float damage = cir.getReturnValue();
        float newAmount = damage;

        if (attacker == null) return;

        if ((lastDamageTaken > 0 || !ReBalance.CONFIG.should_ignore_first_hit) && ReBalance.CONFIG.enable_dynamic_rebalance && amount >= ReBalance.CONFIG.dynamic_reduction_start) {
            if (!(entity instanceof PlayerEntity)) {
                World world = attacker.getWorld();
                float maxHp = getMaxHealth();

                float thresholdCheck = amount / maxHp;
                
                float ddrAttackSpeedWeight = ReBalance.CONFIG.attack_speed_weight;
                float reductionamount = ReBalance.CONFIG.reduction_amount / 100;
                float healththreshold = ReBalance.CONFIG.health_threshold / 100;
                float damageFrequency = 0.01f + (world.getTime() - lastDamageTime) / (damage * ddrAttackSpeedWeight);
                float healthPercent = Math.min((damage / maxHp) * damage, 0.9f * damage);
                float minimumHp = ReBalance.CONFIG.health_minimum;

                if (thresholdCheck > healththreshold && maxHp >= minimumHp) {
                    float damageReduction = Math.min((healthPercent * reductionamount), (damage / 2));
                    newAmount = ReBalance.CONFIG.dynamic_reduction_start + Math.max((damage - damageReduction), 1) * Math.max(Math.min(damageFrequency, 1.0f), 0.3f);
                    
                    if (ReBalance.CONFIG.enable_debug && attacker instanceof PlayerEntity) {
                        attacker.sendMessage(Text.literal("§fDamage reduced from §6" + damage + " §fto§a " + newAmount + " §fusing DR: §6" + damageReduction + "§f & SDR: §b" + damageFrequency));
                        attacker.sendMessage(Text.literal("§6Damage§f:" + damage));
                        attacker.sendMessage(Text.literal("§6Threshold§f:" + thresholdCheck));
                        attacker.sendMessage(Text.literal("§6Health Threshold§f:" + healththreshold));
                        attacker.sendMessage(Text.literal("§6Health Percent§f:" + healthPercent));
                        attacker.sendMessage(Text.literal("§6Minimum HP§f:" + minimumHp));
                        attacker.sendMessage(Text.literal("§6Max HP§f:" + maxHp));
                        attacker.sendMessage(Text.literal("§6Damage Frequency§f:" + damageFrequency));
                        attacker.sendMessage(Text.literal("§6Damage Reduction§f:" + damageReduction));
                        attacker.sendMessage(Text.literal("§6Reduction Amount§f:" + reductionamount));
                        attacker.sendMessage(Text.literal("§bFinal Amount§f:" + newAmount));
                    }
                }                
            }
        }

        if (attacker instanceof PlayerEntity) {
            if (amount > ReBalance.CONFIG.global_reduction_start && ReBalance.CONFIG.enable_global_reduction)
                newAmount *= ReBalance.CONFIG.global_reduction_multiplier;
                newAmount += ReBalance.CONFIG.global_reduction_start;

            if (ReBalance.CONFIG.enable_pvp_rebalance && entity instanceof PlayerEntity) {
                newAmount *= ReBalance.CONFIG.pvp_damage_multiplier;
            }
        }

        cir.setReturnValue(newAmount);
    }
}
