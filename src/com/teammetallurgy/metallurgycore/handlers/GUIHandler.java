package com.teammetallurgy.metallurgycore.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.teammetallurgy.metallurgycore.machines.TileEntityMetallurgy;

import cpw.mods.fml.common.network.IGuiHandler;

public abstract class GUIHandler implements IGuiHandler
{

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityMetallurgy)
        {
            TileEntityMetallurgy te = (TileEntityMetallurgy) tileEntity;
            return this.getGui(ID, player, te);
        }
        return null;
    }

    public abstract Object getContainer(int ID, EntityPlayer player, TileEntityMetallurgy te);

    public abstract Object getGui(int ID, EntityPlayer player, TileEntityMetallurgy te);

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityMetallurgy)
        {
            TileEntityMetallurgy te = (TileEntityMetallurgy) tileEntity;
            return this.getContainer(ID, player, te);
        }
        return null;
    }

}