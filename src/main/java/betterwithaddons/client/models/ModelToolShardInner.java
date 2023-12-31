package betterwithaddons.client.models;

import betterwithaddons.lib.Reference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector4f;
import java.util.*;

public class ModelToolShardInner implements IModel {
    private final ImmutableList<ResourceLocation> textures;

    // private static final float NORTH_Z = 7.496f / 16f;
    // private static final float SOUTH_Z = 8.504f / 16f;
    // private static final float Z_OFFSET = 0.002f / 16f;

    public ModelToolShardInner(ImmutableList<ResourceLocation> textures)
    {
        this.textures = textures;
    }

    @SuppressWarnings("unused")
    private static ImmutableList<ResourceLocation> getTextureListFromMap(ImmutableMap<String, String> textures)
    {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        for(int i = 0; i < textures.size(); i++)
        {
            if(textures.containsKey("layer" + i))
            {
                builder.add(new ResourceLocation(textures.get("layer" + i)));
            }
        }
        return builder.build();
    }

    @Override
    public ModelToolShardInner retexture(ImmutableMap<String, String> textures)
    {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        for(int i = 0; i < textures.size() + this.textures.size(); i++)
        {
            if(textures.containsKey("layer" + i))
            {
                builder.add(new ResourceLocation(textures.get("layer" + i)));
            }
            else if(i < this.textures.size())
            {
                builder.add(this.textures.get(i));
            }
        }
        return new ModelToolShardInner(builder.build());
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    public Collection<ResourceLocation> getTextures()
    {
        return textures;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        Optional<TRSRTransformation> transform = state.apply(Optional.empty());
        for(int i = 0; i < textures.size(); i++)
        {
            ResourceLocation tex = textures.get(i);
            if(tex.toString().equals("minecraft:missingno"))
                continue;
            TextureAtlasSprite sprite = bakedTextureGetter.apply(tex);

            String breakLocation = new ResourceLocation(Reference.MOD_ID,"items/breakmask").toString();
            TextureAtlasSprite breakTemplate = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(breakLocation);

            builder.addAll(getQuadsForSprite(i, breakTemplate, sprite, format, transform));
            //builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, breakTemplate, sprite, NORTH_Z + Z_OFFSET * i, EnumFacing.NORTH, 0xffffffff));
            // builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, breakTemplate, sprite, SOUTH_Z - Z_OFFSET * i, EnumFacing.SOUTH, 0xffffffff));
        }
        TextureAtlasSprite particle = bakedTextureGetter.apply(textures.isEmpty() ? new ResourceLocation("missingno") : textures.get(0));
        ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> map = PerspectiveMapWrapper.getTransforms(state);
        return new BakedItemModel(builder.build(), particle, Maps.immutableEnumMap(map), ItemOverrideList.NONE, true);
    }

    public static ImmutableList<BakedQuad> getQuadsForSprite(int tint, TextureAtlasSprite template, TextureAtlasSprite sprite, VertexFormat format, Optional<TRSRTransformation> transform)
    {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        int uSpriteMax = sprite.getIconWidth();
        int vSpriteMax = sprite.getIconHeight();

        int uMaskMax = template.getIconWidth();
        int vMaskMax = template.getIconHeight();

        int uMax = Math.max(uMaskMax,uSpriteMax);
        int vMax = Math.max(vMaskMax,vSpriteMax);

        /*BitSet faces = new BitSet((uMax + 1) * (vMax + 1) * 4);
        for(int f = 0; f < sprite.getFrameCount(); f++)
        {
            int[] pixels = sprite.getFrameTextureData(f)[0];
            boolean ptu;
            boolean[] ptv = new boolean[uMax];
            Arrays.fill(ptv, true);
            for(int v = 0; v < vMax; v++)
            {
                ptu = true;
                for(int u = 0; u < uMax; u++)
                {
                    boolean t = isTransparent(pixels, uMax, vMax, u, v);
                    if(ptu && !t) // left - transparent, right - opaque
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.WEST, tint, sprite, uMax, vMax, u, v);
                    }
                    if(!ptu && t) // left - opaque, right - transparent
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.EAST, tint, sprite, uMax, vMax, u, v);
                    }
                    if(ptv[u] && !t) // up - transparent, down - opaque
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.UP, tint, sprite, uMax, vMax, u, v);
                    }
                    if(!ptv[u] && t) // up - opaque, down - transparent
                    {
                        addSideQuad(builder, faces, format, transform, EnumFacing.DOWN, tint, sprite, uMax, vMax, u, v);
                    }
                    ptu = t;
                    ptv[u] = t;
                }
                if(!ptu) // last - opaque
                {
                    addSideQuad(builder, faces, format, transform, EnumFacing.EAST, tint, sprite, uMax, vMax, uMax, v);
                }
            }
            // last line
            for(int u = 0; u < uMax; u++)
            {
                if(!ptv[u])
                {
                    addSideQuad(builder, faces, format, transform, EnumFacing.DOWN, tint, sprite, uMax, vMax, u, vMax);
                }
            }
        }*/
        // front
        /*builder.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint,
                0, 0, 7.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
                0, 1, 7.5f / 16f, sprite.getMinU(), sprite.getMinV(),
                1, 1, 7.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
                1, 0, 7.5f / 16f, sprite.getMaxU(), sprite.getMaxV()
        ));
        // back
        builder.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint,
                0, 0, 8.5f / 16f, sprite.getMinU(), sprite.getMaxV(),
                1, 0, 8.5f / 16f, sprite.getMaxU(), sprite.getMaxV(),
                1, 1, 8.5f / 16f, sprite.getMaxU(), sprite.getMinV(),
                0, 1, 8.5f / 16f, sprite.getMinU(), sprite.getMinV()
        ));*/

        float un = uSpriteMax / 16f;
        float vn = vSpriteMax / 16f;

        for(int f = 0; f < sprite.getFrameCount(); f++)
        {
            int[] spritepixels = sprite.getFrameTextureData(f)[0];
            int[] maskpixels = template.getFrameTextureData(0)[0];
            for(int v = 0; v < vMax; v++)
            {
                for(int u = 0; u < uMax; u++)
                {
                    int spriteindex = mapToSmallerPixel(u,v,uMax,vMax,uSpriteMax,vSpriteMax);
                    int maskindex = mapToSmallerPixel(u,v,uMax,vMax,uMaskMax,vMaskMax);

                    if(!isTransparent(spritepixels,maskpixels,spriteindex,maskindex))
                    {
                        int color = maskpixels[maskindex];//multiplyColors(spritepixels[spriteindex],maskpixels[maskindex]);
                        float x1 = u / (float)uMax;
                        float x2 = (u + 1) / (float)uMax;
                        float y1 = (vMax - 1 - v) / (float)vMax;
                        float y2 = (vMax - v) / (float)vMax;

                        float u1 = sprite.getInterpolatedU(u / un);//sprite.getMinU() + u * un;
                        float u2 = sprite.getInterpolatedU((u+1) / un);//sprite.getMinU() + (u+1) * un;
                        float v1 = sprite.getInterpolatedV(v / vn);//sprite.getMinV() + v * vn;
                        float v2 = sprite.getInterpolatedV((v+1) / vn);//sprite.getMinV() + (v+1) * vn;

                        //front
                        builder.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint, color,
                                x1, y1, 7.5f / 16f, u1, v2,
                                x1, y2, 7.5f / 16f, u1, v1,
                                x2, y2, 7.5f / 16f, u2, v1,
                                x2, y1, 7.5f / 16f, u2, v2
                        ));
                        // back
                        builder.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint, color,
                                x1, y1, 8.5f / 16f, u1, v2,
                                x2, y1, 8.5f / 16f, u2, v2,
                                x2, y2, 8.5f / 16f, u2, v1,
                                x1, y2, 8.5f / 16f, u1, v1
                        ));
                        // north
                        builder.add(buildQuad(format, transform, EnumFacing.NORTH, sprite, tint, color,
                                x2, y1, 7.5f / 16f, u2, v2,
                                x2, y1, 8.5f / 16f, u2, v1,
                                x1, y1, 8.5f / 16f, u1, v1,
                                x1, y1, 7.5f / 16f, u1, v2
                        ));
                        // south
                        builder.add(buildQuad(format, transform, EnumFacing.SOUTH, sprite, tint, color,
                                x1, y2, 7.5f / 16f, u1, v2,
                                x1, y2, 8.5f / 16f, u1, v1,
                                x2, y2, 8.5f / 16f, u2, v1,
                                x2, y2, 7.5f / 16f, u2, v2
                        ));
                        builder.add(buildQuad(format, transform, EnumFacing.EAST, sprite, tint, color,
                                x2, y1, 7.5f / 16f, u1, v2,
                                x2, y2, 7.5f / 16f, u1, v1,
                                x2, y2, 8.5f / 16f, u2, v1,
                                x2, y1, 8.5f / 16f, u2, v2
                        ));
                        builder.add(buildQuad(format, transform, EnumFacing.WEST, sprite, tint, color,
                                x1, y1, 8.5f / 16f, u2, v2,
                                x1, y2, 8.5f / 16f, u2, v1,
                                x1, y2, 7.5f / 16f, u1, v1,
                                x1, y1, 7.5f / 16f, u1, v2
                        ));
                    }
                }
            }
        }

        return builder.build();
    }

    @SuppressWarnings("unused")
    private static int multiplyColors(int color1, int color2)
    {
        //associativity of multiplication
        int r = ((color1 & 0xFF) * ((color2 & 0xFF))) / 0xFF;
        int g = (((color1 >> 8) & 0xFF) * ((color2 >> 8) & 0xFF)) / 0xFF;
        int b = (((color1 >> 16) & 0xFF) * ((color2 >> 16) & 0xFF)) / 0xFF;
        int a = (((color1 >> 24) & 0xFF) * ((color2 >> 24) & 0xFF)) / 0xFF;

        return r | (g << 8) | (b << 16) | (a << 24);
    }

    private static int getPixelIndex(int u, int v, int uMax, int vMax)
    {
        //return u + (vMax - 1 - v) * uMax;
        return u + v * vMax;
    }

    //uMax must be multiple of uMin
    //vMax must be multiple of vMin
    private static int mapToSmallerPixel(int u, int v, int uMax, int vMax, int uMin, int vMin)
    {
        int uMod = uMax / uMin;
        int vMod = vMax / vMin;

        return getPixelIndex((u / uMod) % uMin, (v / vMod) % uMin, uMin, vMin);
    }

    private static boolean isTransparent(int[] pixels, int[] maskpixels, int index, int maskindex)
    {
        return (pixels[index] >> 24 & 0xFF) == 0 || (maskpixels[maskindex] >> 24 & 0xFF) == 0;
    }

    @SuppressWarnings("unused")
    private static void addSideQuad(ImmutableList.Builder<BakedQuad> builder, BitSet faces, VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int uMax, int vMax, int u, int v)
    {
        int si = side.ordinal();
        if(si > 4) si -= 2;
        int index = (vMax + 1) * ((uMax + 1) * si + u) + v;
        if(!faces.get(index))
        {
            faces.set(index);
            builder.add(buildSideQuad(format, transform, side, tint, sprite, u, v));
        }
    }

    private static BakedQuad buildSideQuad(VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int tint, TextureAtlasSprite sprite, int u, int v)
    {
        final float eps0 = 30e-5f;
        final float eps1 = 45e-5f;
        final float eps2 = .5f;
        final float eps3 = .5f;
        float x0 = (float)u / sprite.getIconWidth();
        float y0 = (float)v / sprite.getIconHeight();
        float x1 = x0, y1 = y0;
        float z1 = 7.5f / 16f - eps1, z2 = 8.5f / 16f + eps1;
        switch(side)
        {
            case WEST:
                z1 = 8.5f / 16f + eps1;
                z2 = 7.5f / 16f - eps1;
            case EAST:
                y1 = (v + 1f) / sprite.getIconHeight();
                break;
            case DOWN:
                z1 = 8.5f / 16f + eps1;
                z2 = 7.5f / 16f - eps1;
            case UP:
                x1 = (u + 1f) / sprite.getIconWidth();
                break;
            default:
                throw new IllegalArgumentException("can't handle z-oriented side");
        }
        float u0 = 16f * (x0 - side.getDirectionVec().getX() * eps3 / sprite.getIconWidth());
        float u1 = 16f * (x1 - side.getDirectionVec().getX() * eps3 / sprite.getIconWidth());
        float v0 = 16f * (1f - y0 - side.getDirectionVec().getY() * eps3 / sprite.getIconHeight());
        float v1 = 16f * (1f - y1 - side.getDirectionVec().getY() * eps3 / sprite.getIconHeight());
        switch(side)
        {
            case WEST:
            case EAST:
                y0 -= eps1;
                y1 += eps1;
                v0 -= eps2 / sprite.getIconHeight();
                v1 += eps2 / sprite.getIconHeight();
                break;
            case DOWN:
            case UP:
                x0 -= eps1;
                x1 += eps1;
                u0 += eps2 / sprite.getIconWidth();
                u1 -= eps2 / sprite.getIconWidth();
                break;
            default:
                throw new IllegalArgumentException("can't handle z-oriented side");
        }
        switch(side)
        {
            case WEST:
                x0 += eps0;
                x1 += eps0;
                break;
            case EAST:
                x0 -= eps0;
                x1 -= eps0;
                break;
            case DOWN:
                y0 -= eps0;
                y1 -= eps0;
                break;
            case UP:
                y0 += eps0;
                y1 += eps0;
                break;
            default:
                throw new IllegalArgumentException("can't handle z-oriented side");
        }
        return buildQuad(
                format, transform, side.getOpposite(), sprite, tint, 0,// getOpposite is related either to the swapping of V direction, or something else
                x0, y0, z1, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0),
                x1, y1, z1, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
                x1, y1, z2, sprite.getInterpolatedU(u1), sprite.getInterpolatedV(v1),
                x0, y0, z2, sprite.getInterpolatedU(u0), sprite.getInterpolatedV(v0)
        );
    }

    private static final BakedQuad buildQuad(
            VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, TextureAtlasSprite sprite, int tint, int color,
            float x0, float y0, float z0, float u0, float v0,
            float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2,
            float x3, float y3, float z3, float u3, float v3)
    {
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);
        builder.setQuadTint(tint);
        builder.setQuadOrientation(side);
        builder.setTexture(sprite);
        putVertex(builder, format, transform, side, color, x0, y0, z0, u0, v0);
        putVertex(builder, format, transform, side, color, x1, y1, z1, u1, v1);
        putVertex(builder, format, transform, side, color, x2, y2, z2, u2, v2);
        putVertex(builder, format, transform, side, color, x3, y3, z3, u3, v3);
        return builder.build();
    }

    private static void putVertex(UnpackedBakedQuad.Builder builder, VertexFormat format, Optional<TRSRTransformation> transform, EnumFacing side, int color, float x, float y, float z, float u, float v)
    {
        Vector4f vec = new Vector4f();
        for(int e = 0; e < format.getElementCount(); e++)
        {
            switch(format.getElement(e).getUsage())
            {
                case POSITION:
                    if(transform.isPresent())
                    {
                        vec.x = x;
                        vec.y = y;
                        vec.z = z;
                        vec.w = 1;
                        transform.get().getMatrix().transform(vec);
                        builder.put(e, vec.x, vec.y, vec.z, vec.w);
                    }
                    else
                    {
                        builder.put(e, x, y, z, 1);
                    }
                    break;
                case COLOR:
                    float r = ((color >> 16) & 0xFF) / 255f; // red
                    float g = ((color >> 8) & 0xFF) / 255f; // green
                    float b = ((color >> 0) & 0xFF) / 255f; // blue
                    float a = ((color >> 24) & 0xFF) / 255f; // alpha
                    builder.put(e, r, g, b, a);
                    break;
                case UV: if(format.getElement(e).getIndex() == 0)
                {
                    builder.put(e, u, v, 0f, 1f);
                    break;
                }
                case NORMAL:
                    builder.put(e, (float)side.getXOffset(), (float)side.getYOffset(), (float)side.getZOffset(), 0f);
                    break;
                default:
                    builder.put(e);
                    break;
            }
        }
    }

    /*private static final class BakedItemModel implements IBakedModel {
        private final ImmutableList<BakedQuad> quads;
        private final TextureAtlasSprite particle;
        private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
        private final IBakedModel otherModel;
        private final boolean isCulled;

        public BakedItemModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<TransformType, TRSRTransformation> transforms, IBakedModel otherModel) {
            this.quads = quads;
            this.particle = particle;
            this.transforms = transforms;
            if(otherModel != null)
            {
                this.otherModel = otherModel;
                this.isCulled = true;
            }
            else
            {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                for(BakedQuad quad : quads)
                {
                    if(quad.getFace() == EnumFacing.SOUTH)
                    {
                        builder.add(quad);
                    }
                }
                this.otherModel = new BakedItemModel(builder.build(), particle, transforms, this);
                isCulled = false;
            }
        }

        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
            Pair<? extends IBakedModel, Matrix4f> pair = PerspectiveMapWrapper.handlePerspective(this, transforms, type);
            if(type == TransformType.GUI && !isCulled && pair.getRight() == null)
            {
                return Pair.of(otherModel, null);
            }
            else if(type != TransformType.GUI && isCulled)
            {
                return Pair.of(otherModel, pair.getRight());
            }
            return pair;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            if(side == null) return quads;
            return ImmutableList.of();
        }

        @Override
        public boolean isAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return particle;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return ItemCameraTransforms.DEFAULT;
        }

        @Override
        public ItemOverrideList getOverrides() {
            return ItemOverrideList.NONE;
        }
    }*/
}
