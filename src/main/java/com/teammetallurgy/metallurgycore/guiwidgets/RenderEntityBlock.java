package com.teammetallurgy.metallurgycore.guiwidgets;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class RenderEntityBlock extends Render
{
    public static class RenderInfo
    {

        public Block baseBlock = Blocks.sand;
        public int brightness = -1;
        public float light = -1f;
        public double maxX;
        public double maxY;
        public double maxZ;
        public double minX;
        public double minY;
        public double minZ;
        public boolean[] renderSide = new boolean[6];
        public IIcon texture = null;
        public IIcon[] textureArray = null;

        public RenderInfo()
        {
            this.setRenderAllSides();
        }

        public RenderInfo(final Block template, final IIcon[] texture)
        {
            this();
            this.baseBlock = template;
            this.textureArray = texture;
        }

        public RenderInfo(final float minX, final float minY, final float minZ, final float maxX, final float maxY, final float maxZ)
        {
            this();
            this.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
        }

        public float getBlockBrightness(final IBlockAccess iblockaccess, final int i, final int j, final int k)
        {
            return this.baseBlock.getMixedBrightnessForBlock(iblockaccess, i, j, k);
        }

        public IIcon getBlockTextureFromSide(int i)
        {
            if (this.texture != null) { return this.texture; }
            if (this.textureArray == null || this.textureArray.length == 0)
            {
                return this.baseBlock.getBlockTextureFromSide(i);
            }
            else
            {
                if (i >= this.textureArray.length)
                {
                    i = 0;
                }
                return this.textureArray[i];
            }
        }

        public void reverseX()
        {
            final double temp = this.minX;
            this.minX = 1 - this.maxX;
            this.maxX = 1 - temp;
        }

        public void reverseZ()
        {
            final double temp = this.minZ;
            this.minZ = 1 - this.maxZ;
            this.maxZ = 1 - temp;
        }

        public void rotate()
        {
            double temp = this.minX;
            this.minX = this.minZ;
            this.minZ = temp;

            temp = this.maxX;
            this.maxX = this.maxZ;
            this.maxZ = temp;
        }

        public final void setBounds(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ)
        {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public final void setRenderAllSides()
        {
            Arrays.fill(this.renderSide, true);
        }

        public final void setRenderSingleSide(final int side)
        {
            Arrays.fill(this.renderSide, false);
            this.renderSide[side] = true;
        }
    }

    public static RenderEntityBlock INSTANCE = new RenderEntityBlock();

    private RenderEntityBlock()
    {
    }

    @Override
    public void doRender(final Entity entity, final double i, final double j, final double k, final float f, final float f1)
    {
        this.doRenderBlock((EntityBlock) entity, i, j, k);
    }

    public void doRenderBlock(final EntityBlock entity, final double i, final double j, final double k)
    {
        if (entity.isDead) { return; }

        this.shadowSize = entity.shadowSize;
        final World world = entity.worldObj;
        final RenderInfo util = new RenderInfo();
        util.texture = entity.texture;
        this.bindTexture(TextureMap.locationBlocksTexture);

        for (int iBase = 0; iBase < entity.iSize; ++iBase)
        {
            for (int jBase = 0; jBase < entity.jSize; ++jBase)
            {
                for (int kBase = 0; kBase < entity.kSize; ++kBase)
                {

                    util.minX = 0;
                    util.minY = 0;
                    util.minZ = 0;

                    final double remainX = entity.iSize - iBase;
                    final double remainY = entity.jSize - jBase;
                    final double remainZ = entity.kSize - kBase;

                    util.maxX = remainX > 1.0 ? 1.0 : remainX;
                    util.maxY = remainY > 1.0 ? 1.0 : remainY;
                    util.maxZ = remainZ > 1.0 ? 1.0 : remainZ;

                    GL11.glPushMatrix();
                    GL11.glTranslatef((float) i, (float) j, (float) k);
                    GL11.glRotatef(entity.rotationX, 1, 0, 0);
                    GL11.glRotatef(entity.rotationY, 0, 1, 0);
                    GL11.glRotatef(entity.rotationZ, 0, 0, 1);
                    GL11.glTranslatef(iBase, jBase, kBase);

                    int lightX, lightY, lightZ;

                    lightX = (int) (Math.floor(entity.posX) + iBase);
                    lightY = (int) (Math.floor(entity.posY) + jBase);
                    lightZ = (int) (Math.floor(entity.posZ) + kBase);

                    GL11.glDisable(2896 /* GL_LIGHTING */);
                    this.renderBlock(util, world, 0, 0, 0, lightX, lightY, lightZ, false, true);
                    GL11.glEnable(2896 /* GL_LIGHTING */);
                    GL11.glPopMatrix();

                }
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(final Entity entity)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void renderBlock(final RenderInfo info, final IBlockAccess blockAccess, final double x, final double y, final double z, final int lightX, final int lightY, final int lightZ,
            boolean doLight, final boolean doTessellating)
    {
        final float lightBottom = 0.5F;
        final float lightTop = 1.0F;
        final float lightEastWest = 0.8F;
        final float lightNorthSouth = 0.6F;

        final Tessellator tessellator = Tessellator.instance;

        if (blockAccess == null)
        {
            doLight = false;
        }

        if (doTessellating && !tessellator.renderingWorldRenderer)
        {
            tessellator.startDrawingQuads();
        }

        float light = 0;
        if (doLight)
        {
            if (info.light < 0)
            {
                light = info.baseBlock.getMixedBrightnessForBlock(blockAccess, lightX, lightY, lightZ);
                light = light + (1.0f - light) * 0.4f;
            }
            else
            {
                light = info.light;
            }
            int brightness = 0;
            if (info.brightness < 0)
            {
                brightness = info.baseBlock.getMixedBrightnessForBlock(blockAccess, lightX, lightY, lightZ);
            }
            else
            {
                brightness = info.brightness;
            }
            tessellator.setBrightness(brightness);
            tessellator.setColorOpaque_F(lightBottom * light, lightBottom * light, lightBottom * light);
        }
        else
        {
            // tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            if (info.brightness >= 0)
            {
                tessellator.setBrightness(info.brightness);
            }
        }

        this.field_147909_c.setRenderBounds(info.minX, info.minY, info.minZ, info.maxX, info.maxY, info.maxZ);

        if (info.renderSide[0])
        {
            this.field_147909_c.renderFaceYNeg(info.baseBlock, x, y, z, info.getBlockTextureFromSide(0));
        }

        if (doLight)
        {
            tessellator.setColorOpaque_F(lightTop * light, lightTop * light, lightTop * light);
        }

        if (info.renderSide[1])
        {
            this.field_147909_c.renderFaceYPos(info.baseBlock, x, y, z, info.getBlockTextureFromSide(1));
        }

        if (doLight)
        {
            tessellator.setColorOpaque_F(lightEastWest * light, lightEastWest * light, lightEastWest * light);
        }

        if (info.renderSide[2])
        {
            this.field_147909_c.renderFaceZNeg(info.baseBlock, x, y, z, info.getBlockTextureFromSide(2));
        }

        if (doLight)
        {
            tessellator.setColorOpaque_F(lightEastWest * light, lightEastWest * light, lightEastWest * light);
        }

        if (info.renderSide[3])
        {
            this.field_147909_c.renderFaceZPos(info.baseBlock, x, y, z, info.getBlockTextureFromSide(3));
        }

        if (doLight)
        {
            tessellator.setColorOpaque_F(lightNorthSouth * light, lightNorthSouth * light, lightNorthSouth * light);
        }

        if (info.renderSide[4])
        {
            this.field_147909_c.renderFaceXNeg(info.baseBlock, x, y, z, info.getBlockTextureFromSide(4));
        }

        if (doLight)
        {
            tessellator.setColorOpaque_F(lightNorthSouth * light, lightNorthSouth * light, lightNorthSouth * light);
        }

        if (info.renderSide[5])
        {
            this.field_147909_c.renderFaceXPos(info.baseBlock, x, y, z, info.getBlockTextureFromSide(5));
        }

        if (doTessellating && tessellator.renderingWorldRenderer)
        {
            tessellator.draw();
        }
    }

    public void renderBlock(final RenderInfo info, final IBlockAccess blockAccess, final int x, final int y, final int z, final boolean doLight, final boolean doTessellating)
    {
        this.renderBlock(info, blockAccess, x, y, z, x, y, z, doLight, doTessellating);
    }
}
