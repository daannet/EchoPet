package com.github.dsh105.echopet.entity.pet.blaze;

import net.minecraft.server.v1_6_R2.*;

import com.github.dsh105.echopet.entity.pet.EntityPet;
import com.github.dsh105.echopet.entity.pet.Pet;
import com.github.dsh105.echopet.entity.pet.SizeCategory;

public class EntityBlazePet extends EntityPet {
	
	public EntityBlazePet(World world, Pet pet) {
		super(world, pet);
		this.a(0.6F, 0.7F);
		this.fireProof = true;
	}
	
	public void setOnFire(boolean flag) {
		this.datawatcher.watch(16, (byte) (flag ? 1 : 0));
		((BlazePet) pet).onFire = flag;
	}
	
	protected void a() {
        super.a();
        this.datawatcher.a(16, new Byte((byte) 0));
    }
	
	@Override
	protected String r() {
        return "mob.blaze.breathe";
    }
	
	@Override
	protected String aO() {
		return "mob.blaze.death";
	}
	
	@Override
	public SizeCategory getSizeCategory() {
		return SizeCategory.REGULAR;
	}
}
