package com.supermanitu.advanceddispensers.lib;

import java.util.Random;

import com.supermanitu.advanceddispensers.breaker.BreakerTier;
import com.supermanitu.advanceddispensers.breaker.TileEntityBreaker;
import com.supermanitu.advanceddispensers.main.AdvancedDispensersGuiHandler;
import com.supermanitu.advanceddispensers.main.AdvancedDispensersMod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockAdvancedDispensers extends BlockContainer
{
	public static final PropertyEnum PROPERTYFACING = PropertyEnum.create("facing", EnumFacing.class);
	
	public BlockAdvancedDispensers(Material material) 
	{
		super(material);
		this.setCreativeTab(AdvancedDispensersMod.advancedDispensersTab);
		this.setUnlocalizedName(getName());
	}
	
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.SOLID;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return true;
	}
	
	@Override
	public boolean isFullCube()
	{
		return true;
	}
	
	@Override
	public int getRenderType()
	{
		return 3;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing facing = EnumFacing.getFront(meta & 0x7);
		return this.getDefaultState().withProperty(PROPERTYFACING, facing);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		EnumFacing facing = (EnumFacing)state.getValue(PROPERTYFACING);
		return facing.getIndex();
	}
	
	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {PROPERTYFACING});
	}
	
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase player)
	{
		EnumFacing facing = BlockPistonBase.getFacingFromEntity(world, pos, player);
		
		return this.getDefaultState().withProperty(PROPERTYFACING, facing);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote) 
		{
	        player.openGui(AdvancedDispensersMod.instance, this.getGuiID(), world, pos.getX(), pos.getY(), pos.getZ());
	    }
	    return true;
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		world.scheduleUpdate(pos, this, this.tickRate(world));
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        if (!world.isRemote)
        {
        	if (world.isBlockPowered(pos) || world.isBlockPowered(pos.up()))
    		{
        		((TileEntityAdvancedDispensers)world.getTileEntity(pos)).onReceiveRedstoneSignal(world, pos, state, rand);
    		}
        	else
        	{
        		((TileEntityAdvancedDispensers)world.getTileEntity(pos)).onNotReceiveRedstoneSignal(world, pos, state, rand);
        	}
        }
    }
	
	@Override
	public int tickRate(World world)
	{
		return 4;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntityAdvancedDispensers tileEntity = (TileEntityAdvancedDispensers) world.getTileEntity(pos);
		InventoryHelper.dropInventoryItems(world, pos, tileEntity);
		super.breakBlock(world, pos, state);
	}
	
	public abstract Object[] getRecipe();
	public abstract String getName();
	
	protected abstract int getGuiID();
}
