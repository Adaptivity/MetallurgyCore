package com.teammetallurgy.metallurgycore.machines;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class BlockMetallurgyCore extends BlockContainer
{

    public BlockMetallurgyCore(int id)
    {
        super(id, Material.rock);
    }

    @Override
    public abstract CreativeTabs getCreativeTabToDisplayOn();

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset)
    {
        if (player.isSneaking()) { return false; }

        world.markBlockForUpdate(x, y, z);
        if (!world.isRemote)
        {
            doOnActivate(world, x, y, z, player, side, xOffset, yOffset, zOffset);
        }

        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int oldID, int oldMeta)
    {
        TileEntityMetallurgy blockTileEntity = (TileEntityMetallurgy) world.getBlockTileEntity(x, y, z);
        if (blockTileEntity != null)
        {
            blockTileEntity.dropContents();
        }
        world.removeBlockTileEntity(x, y, z);
    }

    /**
     * Determines what happens after block as been clicked. Only runs on server
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @param player
     * @param side
     * @param xOffset
     * @param yOffset
     * @param zOffset
     */
    abstract protected void doOnActivate(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset);
}