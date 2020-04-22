// ==================================================================
// This file is part of Render Player API.
//
// Render Player API is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// Render Player API is distributed in the hope that it will be
// useful, but WITHOUT ANY WARRANTY; without even the implied
// warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License and the GNU General Public License along with Render
// Player API. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================
package api.player.render.renderer;

import api.player.render.RenderPlayerAPI;
import api.player.render.asm.interfaces.IPlayerRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;

public final class PlayerRendererAPI
{
    private final static Class<?>[] Class = new Class[]{PlayerRendererAPI.class};
    private final static Class<?>[] Classes = new Class[]{PlayerRendererAPI.class, String.class};
    private static boolean isCreated;
    private static final Logger logger = Logger.getLogger("PlayerRendererAPI");
    protected final IPlayerRenderer iPlayerRenderer;
    private final static Set<String> keys = new HashSet<>();
    private final static Map<String, String> keysToVirtualIds = new HashMap<>();
    private final static Set<Class<?>> dynamicTypes = new HashSet<>();
    private final static Map<Class<?>, Map<String, Method>> virtualDynamicHookMethods = new HashMap<>();
    private final static Map<Class<?>, Map<String, Method>> beforeDynamicHookMethods = new HashMap<>();
    private final static Map<Class<?>, Map<String, Method>> overrideDynamicHookMethods = new HashMap<>();
    private final static Map<Class<?>, Map<String, Method>> afterDynamicHookMethods = new HashMap<>();
    private final static List<String> beforeLocalConstructingHookTypes = new LinkedList<>();
    private final static List<String> afterLocalConstructingHookTypes = new LinkedList<>();
    private static final Map<String, List<String>> beforeDynamicHookTypes = new Hashtable<>(0);
    private static final Map<String, List<String>> overrideDynamicHookTypes = new Hashtable<>(0);
    private static final Map<String, List<String>> afterDynamicHookTypes = new Hashtable<>(0);
    private PlayerRendererBase[] beforeLocalConstructingHooks;
    private PlayerRendererBase[] afterLocalConstructingHooks;
    private final Map<PlayerRendererBase, String> baseObjectsToId = new Hashtable<>();
    private final Map<String, PlayerRendererBase> allBaseObjects = new Hashtable<>();
    private final Set<String> unmodifiableAllBaseIds = Collections.unmodifiableSet(this.allBaseObjects.keySet());
    private static final Map<String, Constructor<?>> allBaseConstructors = new Hashtable<>();
    private static final Set<String> unmodifiableAllIds = Collections.unmodifiableSet(allBaseConstructors.keySet());
    private static final Map<String, String[]> allBaseBeforeLocalConstructingSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeLocalConstructingInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterLocalConstructingSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterLocalConstructingInferiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseBeforeDynamicSuperiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseBeforeDynamicInferiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseOverrideDynamicSuperiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseOverrideDynamicInferiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseAfterDynamicSuperiors = new Hashtable<>(0);
    private static final Map<String, Map<String, String[]>> allBaseAfterDynamicInferiors = new Hashtable<>(0);
    private static boolean initialized = false;

    private static void log(String text)
    {
        System.out.println(text);
        logger.fine(text);
    }

    public static void register(String id, Class<?> baseClass)
    {
        register(id, baseClass, null);
    }

    public static void register(String id, Class<?> baseClass, PlayerRendererBaseSorting baseSorting)
    {
        try {
            register(baseClass, id, baseSorting);
        } catch (RuntimeException exception) {
            if (id != null) {
                log("Render Player: failed to register id '" + id + "'");
            } else {
                log("Render Player: failed to register PlayerRendererBase");
            }

            throw exception;
        }
    }

    private static void register(Class<?> baseClass, String id, PlayerRendererBaseSorting baseSorting)
    {
        if (!isCreated) {
            try {
                Method mandatory = PlayerRenderer.class.getMethod("getPlayerRendererBase", String.class);
                if (mandatory.getReturnType() != PlayerRendererBase.class) {
                    throw new NoSuchMethodException(PlayerRendererBase.class.getName() + " " + PlayerRenderer.class.getName() + ".getPlayerRendererBase(" + String.class.getName() + ")");
                }
            } catch (NoSuchMethodException exception) {
                String[] errorMessageParts = new String[]
                        {
                                "========================================",
                                "The API \"Render Player\" version " + RenderPlayerAPI.VERSION + " of the mod \"Render Player API " + RenderPlayerAPI.VERSION + "\" cannot be created!",
                                "----------------------------------------",
                                "Mandatory member method \"{0} getPlayerRendererBase({3})\" not found in class \"{1}\".",
                                "There are three scenarios this can happen:",
                                "* Minecraft Forge is missing a Render Player API which Minecraft version matches its own.",
                                "  Download and install the latest Render Player API for the Minecraft version you were trying to run.",
                                "* The code of the class \"{2}\" of Render Player API has been modified beyond recognition by another Minecraft Forge mod.",
                                "  Try temporary uninstallation of other mods to find the culprit and uninstall it permanently to fix this specific problem.",
                                "* Render Player API has not been installed correctly.",
                                "  Uninstall Render Player API and install it again following the installation instructions.",
                                "========================================"
                        };

                String baseRenderPlayerClassName = PlayerRendererBase.class.getName();
                String targetClassName = PlayerRenderer.class.getName();
                String targetClassFileName = targetClassName.replace(".", File.separator);
                String stringClassName = String.class.getName();

                for (int i = 0; i < errorMessageParts.length; i++) {
                    errorMessageParts[i] = MessageFormat.format(errorMessageParts[i], baseRenderPlayerClassName, targetClassName, targetClassFileName, stringClassName);
                }

                for (String errorMessagePart : errorMessageParts) {
                    logger.severe(errorMessagePart);
                }

                for (String errorMessagePart : errorMessageParts) {
                    System.err.println(errorMessagePart);
                }

                StringBuilder errorMessage = new StringBuilder("\n\n");
                for (String errorMessagePart : errorMessageParts) {
                    errorMessage.append("\t").append(errorMessagePart).append("\n");
                }

                throw new RuntimeException(errorMessage.toString(), exception);
            }

            log("Render Player " + RenderPlayerAPI.VERSION + " Created");
            isCreated = true;
        }

        if (id == null) {
            throw new NullPointerException("Argument 'id' can not be null");
        }
        if (baseClass == null) {
            throw new NullPointerException("Argument 'baseClass' can not be null");
        }

        Constructor<?> alreadyRegistered = allBaseConstructors.get(id);
        if (alreadyRegistered != null) {
            throw new IllegalArgumentException("The class '" + baseClass.getName() + "' can not be registered with the id '" + id + "' because the class '" + alreadyRegistered.getDeclaringClass().getName() + "' has already been registered with the same id");
        }

        Constructor<?> baseConstructor;
        try {
            baseConstructor = baseClass.getDeclaredConstructor(Classes);
        } catch (Throwable t) {
            try {
                baseConstructor = baseClass.getDeclaredConstructor(Class);
            } catch (Throwable s) {
                throw new IllegalArgumentException("Can not find necessary constructor with one argument of type '" + PlayerRendererAPI.class.getName() + "' and eventually a second argument of type 'String' in the class '" + baseClass.getName() + "'", t);
            }
        }

        allBaseConstructors.put(id, baseConstructor);

        if (baseSorting != null) {
            addSorting(id, allBaseBeforeLocalConstructingSuperiors, baseSorting.getBeforeLocalConstructingSuperiors());
            addSorting(id, allBaseBeforeLocalConstructingInferiors, baseSorting.getBeforeLocalConstructingInferiors());
            addSorting(id, allBaseAfterLocalConstructingSuperiors, baseSorting.getAfterLocalConstructingSuperiors());
            addSorting(id, allBaseAfterLocalConstructingInferiors, baseSorting.getAfterLocalConstructingInferiors());

            addDynamicSorting(id, allBaseBeforeDynamicSuperiors, baseSorting.getDynamicBeforeSuperiors());
            addDynamicSorting(id, allBaseBeforeDynamicInferiors, baseSorting.getDynamicBeforeInferiors());
            addDynamicSorting(id, allBaseOverrideDynamicSuperiors, baseSorting.getDynamicOverrideSuperiors());
            addDynamicSorting(id, allBaseOverrideDynamicInferiors, baseSorting.getDynamicOverrideInferiors());
            addDynamicSorting(id, allBaseAfterDynamicSuperiors, baseSorting.getDynamicAfterSuperiors());
            addDynamicSorting(id, allBaseAfterDynamicInferiors, baseSorting.getDynamicAfterInferiors());

            addSorting(id, allBaseBeforeApplyRotationsSuperiors, baseSorting.getBeforeApplyRotationsSuperiors());
            addSorting(id, allBaseBeforeApplyRotationsInferiors, baseSorting.getBeforeApplyRotationsInferiors());
            addSorting(id, allBaseOverrideApplyRotationsSuperiors, baseSorting.getOverrideApplyRotationsSuperiors());
            addSorting(id, allBaseOverrideApplyRotationsInferiors, baseSorting.getOverrideApplyRotationsInferiors());
            addSorting(id, allBaseAfterApplyRotationsSuperiors, baseSorting.getAfterApplyRotationsSuperiors());
            addSorting(id, allBaseAfterApplyRotationsInferiors, baseSorting.getAfterApplyRotationsInferiors());

            addSorting(id, allBaseBeforeCanRenderNameSuperiors, baseSorting.getBeforeCanRenderNameSuperiors());
            addSorting(id, allBaseBeforeCanRenderNameInferiors, baseSorting.getBeforeCanRenderNameInferiors());
            addSorting(id, allBaseOverrideCanRenderNameSuperiors, baseSorting.getOverrideCanRenderNameSuperiors());
            addSorting(id, allBaseOverrideCanRenderNameInferiors, baseSorting.getOverrideCanRenderNameInferiors());
            addSorting(id, allBaseAfterCanRenderNameSuperiors, baseSorting.getAfterCanRenderNameSuperiors());
            addSorting(id, allBaseAfterCanRenderNameInferiors, baseSorting.getAfterCanRenderNameInferiors());

            addSorting(id, allBaseBeforeGetArmPoseSuperiors, baseSorting.getBeforeGetArmPoseSuperiors());
            addSorting(id, allBaseBeforeGetArmPoseInferiors, baseSorting.getBeforeGetArmPoseInferiors());
            addSorting(id, allBaseOverrideGetArmPoseSuperiors, baseSorting.getOverrideGetArmPoseSuperiors());
            addSorting(id, allBaseOverrideGetArmPoseInferiors, baseSorting.getOverrideGetArmPoseInferiors());
            addSorting(id, allBaseAfterGetArmPoseSuperiors, baseSorting.getAfterGetArmPoseSuperiors());
            addSorting(id, allBaseAfterGetArmPoseInferiors, baseSorting.getAfterGetArmPoseInferiors());

            addSorting(id, allBaseBeforeGetDeathMaxRotationSuperiors, baseSorting.getBeforeGetDeathMaxRotationSuperiors());
            addSorting(id, allBaseBeforeGetDeathMaxRotationInferiors, baseSorting.getBeforeGetDeathMaxRotationInferiors());
            addSorting(id, allBaseOverrideGetDeathMaxRotationSuperiors, baseSorting.getOverrideGetDeathMaxRotationSuperiors());
            addSorting(id, allBaseOverrideGetDeathMaxRotationInferiors, baseSorting.getOverrideGetDeathMaxRotationInferiors());
            addSorting(id, allBaseAfterGetDeathMaxRotationSuperiors, baseSorting.getAfterGetDeathMaxRotationSuperiors());
            addSorting(id, allBaseAfterGetDeathMaxRotationInferiors, baseSorting.getAfterGetDeathMaxRotationInferiors());

            addSorting(id, allBaseBeforeGetEntityModelSuperiors, baseSorting.getBeforeGetEntityModelSuperiors());
            addSorting(id, allBaseBeforeGetEntityModelInferiors, baseSorting.getBeforeGetEntityModelInferiors());
            addSorting(id, allBaseOverrideGetEntityModelSuperiors, baseSorting.getOverrideGetEntityModelSuperiors());
            addSorting(id, allBaseOverrideGetEntityModelInferiors, baseSorting.getOverrideGetEntityModelInferiors());
            addSorting(id, allBaseAfterGetEntityModelSuperiors, baseSorting.getAfterGetEntityModelSuperiors());
            addSorting(id, allBaseAfterGetEntityModelInferiors, baseSorting.getAfterGetEntityModelInferiors());

            addSorting(id, allBaseBeforeGetEntityTextureSuperiors, baseSorting.getBeforeGetEntityTextureSuperiors());
            addSorting(id, allBaseBeforeGetEntityTextureInferiors, baseSorting.getBeforeGetEntityTextureInferiors());
            addSorting(id, allBaseOverrideGetEntityTextureSuperiors, baseSorting.getOverrideGetEntityTextureSuperiors());
            addSorting(id, allBaseOverrideGetEntityTextureInferiors, baseSorting.getOverrideGetEntityTextureInferiors());
            addSorting(id, allBaseAfterGetEntityTextureSuperiors, baseSorting.getAfterGetEntityTextureSuperiors());
            addSorting(id, allBaseAfterGetEntityTextureInferiors, baseSorting.getAfterGetEntityTextureInferiors());

            addSorting(id, allBaseBeforeGetFontRendererFromRenderManagerSuperiors, baseSorting.getBeforeGetFontRendererFromRenderManagerSuperiors());
            addSorting(id, allBaseBeforeGetFontRendererFromRenderManagerInferiors, baseSorting.getBeforeGetFontRendererFromRenderManagerInferiors());
            addSorting(id, allBaseOverrideGetFontRendererFromRenderManagerSuperiors, baseSorting.getOverrideGetFontRendererFromRenderManagerSuperiors());
            addSorting(id, allBaseOverrideGetFontRendererFromRenderManagerInferiors, baseSorting.getOverrideGetFontRendererFromRenderManagerInferiors());
            addSorting(id, allBaseAfterGetFontRendererFromRenderManagerSuperiors, baseSorting.getAfterGetFontRendererFromRenderManagerSuperiors());
            addSorting(id, allBaseAfterGetFontRendererFromRenderManagerInferiors, baseSorting.getAfterGetFontRendererFromRenderManagerInferiors());

            addSorting(id, allBaseBeforeGetRenderManagerSuperiors, baseSorting.getBeforeGetRenderManagerSuperiors());
            addSorting(id, allBaseBeforeGetRenderManagerInferiors, baseSorting.getBeforeGetRenderManagerInferiors());
            addSorting(id, allBaseOverrideGetRenderManagerSuperiors, baseSorting.getOverrideGetRenderManagerSuperiors());
            addSorting(id, allBaseOverrideGetRenderManagerInferiors, baseSorting.getOverrideGetRenderManagerInferiors());
            addSorting(id, allBaseAfterGetRenderManagerSuperiors, baseSorting.getAfterGetRenderManagerSuperiors());
            addSorting(id, allBaseAfterGetRenderManagerInferiors, baseSorting.getAfterGetRenderManagerInferiors());

            addSorting(id, allBaseBeforeGetRenderOffsetSuperiors, baseSorting.getBeforeGetRenderOffsetSuperiors());
            addSorting(id, allBaseBeforeGetRenderOffsetInferiors, baseSorting.getBeforeGetRenderOffsetInferiors());
            addSorting(id, allBaseOverrideGetRenderOffsetSuperiors, baseSorting.getOverrideGetRenderOffsetSuperiors());
            addSorting(id, allBaseOverrideGetRenderOffsetInferiors, baseSorting.getOverrideGetRenderOffsetInferiors());
            addSorting(id, allBaseAfterGetRenderOffsetSuperiors, baseSorting.getAfterGetRenderOffsetSuperiors());
            addSorting(id, allBaseAfterGetRenderOffsetInferiors, baseSorting.getAfterGetRenderOffsetInferiors());

            addSorting(id, allBaseBeforeGetSwingProgressSuperiors, baseSorting.getBeforeGetSwingProgressSuperiors());
            addSorting(id, allBaseBeforeGetSwingProgressInferiors, baseSorting.getBeforeGetSwingProgressInferiors());
            addSorting(id, allBaseOverrideGetSwingProgressSuperiors, baseSorting.getOverrideGetSwingProgressSuperiors());
            addSorting(id, allBaseOverrideGetSwingProgressInferiors, baseSorting.getOverrideGetSwingProgressInferiors());
            addSorting(id, allBaseAfterGetSwingProgressSuperiors, baseSorting.getAfterGetSwingProgressSuperiors());
            addSorting(id, allBaseAfterGetSwingProgressInferiors, baseSorting.getAfterGetSwingProgressInferiors());

            addSorting(id, allBaseBeforeHandleRotationFloatSuperiors, baseSorting.getBeforeHandleRotationFloatSuperiors());
            addSorting(id, allBaseBeforeHandleRotationFloatInferiors, baseSorting.getBeforeHandleRotationFloatInferiors());
            addSorting(id, allBaseOverrideHandleRotationFloatSuperiors, baseSorting.getOverrideHandleRotationFloatSuperiors());
            addSorting(id, allBaseOverrideHandleRotationFloatInferiors, baseSorting.getOverrideHandleRotationFloatInferiors());
            addSorting(id, allBaseAfterHandleRotationFloatSuperiors, baseSorting.getAfterHandleRotationFloatSuperiors());
            addSorting(id, allBaseAfterHandleRotationFloatInferiors, baseSorting.getAfterHandleRotationFloatInferiors());

            addSorting(id, allBaseBeforePreRenderCallbackSuperiors, baseSorting.getBeforePreRenderCallbackSuperiors());
            addSorting(id, allBaseBeforePreRenderCallbackInferiors, baseSorting.getBeforePreRenderCallbackInferiors());
            addSorting(id, allBaseOverridePreRenderCallbackSuperiors, baseSorting.getOverridePreRenderCallbackSuperiors());
            addSorting(id, allBaseOverridePreRenderCallbackInferiors, baseSorting.getOverridePreRenderCallbackInferiors());
            addSorting(id, allBaseAfterPreRenderCallbackSuperiors, baseSorting.getAfterPreRenderCallbackSuperiors());
            addSorting(id, allBaseAfterPreRenderCallbackInferiors, baseSorting.getAfterPreRenderCallbackInferiors());

            addSorting(id, allBaseBeforeRenderSuperiors, baseSorting.getBeforeRenderSuperiors());
            addSorting(id, allBaseBeforeRenderInferiors, baseSorting.getBeforeRenderInferiors());
            addSorting(id, allBaseOverrideRenderSuperiors, baseSorting.getOverrideRenderSuperiors());
            addSorting(id, allBaseOverrideRenderInferiors, baseSorting.getOverrideRenderInferiors());
            addSorting(id, allBaseAfterRenderSuperiors, baseSorting.getAfterRenderSuperiors());
            addSorting(id, allBaseAfterRenderInferiors, baseSorting.getAfterRenderInferiors());

            addSorting(id, allBaseBeforeRenderItemSuperiors, baseSorting.getBeforeRenderItemSuperiors());
            addSorting(id, allBaseBeforeRenderItemInferiors, baseSorting.getBeforeRenderItemInferiors());
            addSorting(id, allBaseOverrideRenderItemSuperiors, baseSorting.getOverrideRenderItemSuperiors());
            addSorting(id, allBaseOverrideRenderItemInferiors, baseSorting.getOverrideRenderItemInferiors());
            addSorting(id, allBaseAfterRenderItemSuperiors, baseSorting.getAfterRenderItemSuperiors());
            addSorting(id, allBaseAfterRenderItemInferiors, baseSorting.getAfterRenderItemInferiors());

            addSorting(id, allBaseBeforeRenderLeftArmSuperiors, baseSorting.getBeforeRenderLeftArmSuperiors());
            addSorting(id, allBaseBeforeRenderLeftArmInferiors, baseSorting.getBeforeRenderLeftArmInferiors());
            addSorting(id, allBaseOverrideRenderLeftArmSuperiors, baseSorting.getOverrideRenderLeftArmSuperiors());
            addSorting(id, allBaseOverrideRenderLeftArmInferiors, baseSorting.getOverrideRenderLeftArmInferiors());
            addSorting(id, allBaseAfterRenderLeftArmSuperiors, baseSorting.getAfterRenderLeftArmSuperiors());
            addSorting(id, allBaseAfterRenderLeftArmInferiors, baseSorting.getAfterRenderLeftArmInferiors());

            addSorting(id, allBaseBeforeRenderNameSuperiors, baseSorting.getBeforeRenderNameSuperiors());
            addSorting(id, allBaseBeforeRenderNameInferiors, baseSorting.getBeforeRenderNameInferiors());
            addSorting(id, allBaseOverrideRenderNameSuperiors, baseSorting.getOverrideRenderNameSuperiors());
            addSorting(id, allBaseOverrideRenderNameInferiors, baseSorting.getOverrideRenderNameInferiors());
            addSorting(id, allBaseAfterRenderNameSuperiors, baseSorting.getAfterRenderNameSuperiors());
            addSorting(id, allBaseAfterRenderNameInferiors, baseSorting.getAfterRenderNameInferiors());

            addSorting(id, allBaseBeforeRenderRightArmSuperiors, baseSorting.getBeforeRenderRightArmSuperiors());
            addSorting(id, allBaseBeforeRenderRightArmInferiors, baseSorting.getBeforeRenderRightArmInferiors());
            addSorting(id, allBaseOverrideRenderRightArmSuperiors, baseSorting.getOverrideRenderRightArmSuperiors());
            addSorting(id, allBaseOverrideRenderRightArmInferiors, baseSorting.getOverrideRenderRightArmInferiors());
            addSorting(id, allBaseAfterRenderRightArmSuperiors, baseSorting.getAfterRenderRightArmSuperiors());
            addSorting(id, allBaseAfterRenderRightArmInferiors, baseSorting.getAfterRenderRightArmInferiors());

            addSorting(id, allBaseBeforeSetModelVisibilitiesSuperiors, baseSorting.getBeforeSetModelVisibilitiesSuperiors());
            addSorting(id, allBaseBeforeSetModelVisibilitiesInferiors, baseSorting.getBeforeSetModelVisibilitiesInferiors());
            addSorting(id, allBaseOverrideSetModelVisibilitiesSuperiors, baseSorting.getOverrideSetModelVisibilitiesSuperiors());
            addSorting(id, allBaseOverrideSetModelVisibilitiesInferiors, baseSorting.getOverrideSetModelVisibilitiesInferiors());
            addSorting(id, allBaseAfterSetModelVisibilitiesSuperiors, baseSorting.getAfterSetModelVisibilitiesSuperiors());
            addSorting(id, allBaseAfterSetModelVisibilitiesInferiors, baseSorting.getAfterSetModelVisibilitiesInferiors());

            addSorting(id, allBaseBeforeShouldRenderSuperiors, baseSorting.getBeforeShouldRenderSuperiors());
            addSorting(id, allBaseBeforeShouldRenderInferiors, baseSorting.getBeforeShouldRenderInferiors());
            addSorting(id, allBaseOverrideShouldRenderSuperiors, baseSorting.getOverrideShouldRenderSuperiors());
            addSorting(id, allBaseOverrideShouldRenderInferiors, baseSorting.getOverrideShouldRenderInferiors());
            addSorting(id, allBaseAfterShouldRenderSuperiors, baseSorting.getAfterShouldRenderSuperiors());
            addSorting(id, allBaseAfterShouldRenderInferiors, baseSorting.getAfterShouldRenderInferiors());
        }

        addMethod(id, baseClass, beforeLocalConstructingHookTypes, "beforeLocalConstructing", EntityRendererManager.class, boolean.class);
        addMethod(id, baseClass, afterLocalConstructingHookTypes, "afterLocalConstructing", EntityRendererManager.class, boolean.class);

        addMethod(id, baseClass, beforeApplyRotationsHookTypes, "beforeApplyRotations", AbstractClientPlayerEntity.class, MatrixStack.class, float.class, float.class, float.class);
        addMethod(id, baseClass, overrideApplyRotationsHookTypes, "applyRotations", AbstractClientPlayerEntity.class, MatrixStack.class, float.class, float.class, float.class);
        addMethod(id, baseClass, afterApplyRotationsHookTypes, "afterApplyRotations", AbstractClientPlayerEntity.class, MatrixStack.class, float.class, float.class, float.class);

        addMethod(id, baseClass, beforeCanRenderNameHookTypes, "beforeCanRenderName", AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, overrideCanRenderNameHookTypes, "canRenderName", AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, afterCanRenderNameHookTypes, "afterCanRenderName", AbstractClientPlayerEntity.class);

        addMethod(id, baseClass, beforeGetArmPoseHookTypes, "beforeGetArmPose", AbstractClientPlayerEntity.class, ItemStack.class, ItemStack.class, Hand.class);
        addMethod(id, baseClass, overrideGetArmPoseHookTypes, "getArmPose", AbstractClientPlayerEntity.class, ItemStack.class, ItemStack.class, Hand.class);
        addMethod(id, baseClass, afterGetArmPoseHookTypes, "afterGetArmPose", AbstractClientPlayerEntity.class, ItemStack.class, ItemStack.class, Hand.class);

        addMethod(id, baseClass, beforeGetDeathMaxRotationHookTypes, "beforeGetDeathMaxRotation", AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, overrideGetDeathMaxRotationHookTypes, "getDeathMaxRotation", AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, afterGetDeathMaxRotationHookTypes, "afterGetDeathMaxRotation", AbstractClientPlayerEntity.class);

        addMethod(id, baseClass, beforeGetEntityModelHookTypes, "beforeGetEntityModel");
        addMethod(id, baseClass, overrideGetEntityModelHookTypes, "getEntityModel");
        addMethod(id, baseClass, afterGetEntityModelHookTypes, "afterGetEntityModel");

        addMethod(id, baseClass, beforeGetEntityTextureHookTypes, "beforeGetEntityTexture", AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, overrideGetEntityTextureHookTypes, "getEntityTexture", AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, afterGetEntityTextureHookTypes, "afterGetEntityTexture", AbstractClientPlayerEntity.class);

        addMethod(id, baseClass, beforeGetFontRendererFromRenderManagerHookTypes, "beforeGetFontRendererFromRenderManager");
        addMethod(id, baseClass, overrideGetFontRendererFromRenderManagerHookTypes, "getFontRendererFromRenderManager");
        addMethod(id, baseClass, afterGetFontRendererFromRenderManagerHookTypes, "afterGetFontRendererFromRenderManager");

        addMethod(id, baseClass, beforeGetRenderManagerHookTypes, "beforeGetRenderManager");
        addMethod(id, baseClass, overrideGetRenderManagerHookTypes, "getRenderManager");
        addMethod(id, baseClass, afterGetRenderManagerHookTypes, "afterGetRenderManager");

        addMethod(id, baseClass, beforeGetRenderOffsetHookTypes, "beforeGetRenderOffset", AbstractClientPlayerEntity.class, float.class);
        addMethod(id, baseClass, overrideGetRenderOffsetHookTypes, "getRenderOffset", AbstractClientPlayerEntity.class, float.class);
        addMethod(id, baseClass, afterGetRenderOffsetHookTypes, "afterGetRenderOffset", AbstractClientPlayerEntity.class, float.class);

        addMethod(id, baseClass, beforeGetSwingProgressHookTypes, "beforeGetSwingProgress", AbstractClientPlayerEntity.class, float.class);
        addMethod(id, baseClass, overrideGetSwingProgressHookTypes, "getSwingProgress", AbstractClientPlayerEntity.class, float.class);
        addMethod(id, baseClass, afterGetSwingProgressHookTypes, "afterGetSwingProgress", AbstractClientPlayerEntity.class, float.class);

        addMethod(id, baseClass, beforeHandleRotationFloatHookTypes, "beforeHandleRotationFloat", AbstractClientPlayerEntity.class, float.class);
        addMethod(id, baseClass, overrideHandleRotationFloatHookTypes, "handleRotationFloat", AbstractClientPlayerEntity.class, float.class);
        addMethod(id, baseClass, afterHandleRotationFloatHookTypes, "afterHandleRotationFloat", AbstractClientPlayerEntity.class, float.class);

        addMethod(id, baseClass, beforePreRenderCallbackHookTypes, "beforePreRenderCallback", AbstractClientPlayerEntity.class, MatrixStack.class, float.class);
        addMethod(id, baseClass, overridePreRenderCallbackHookTypes, "preRenderCallback", AbstractClientPlayerEntity.class, MatrixStack.class, float.class);
        addMethod(id, baseClass, afterPreRenderCallbackHookTypes, "afterPreRenderCallback", AbstractClientPlayerEntity.class, MatrixStack.class, float.class);

        addMethod(id, baseClass, beforeRenderHookTypes, "beforeRender", AbstractClientPlayerEntity.class, float.class, float.class, MatrixStack.class, IRenderTypeBuffer.class, int.class);
        addMethod(id, baseClass, overrideRenderHookTypes, "render", AbstractClientPlayerEntity.class, float.class, float.class, MatrixStack.class, IRenderTypeBuffer.class, int.class);
        addMethod(id, baseClass, afterRenderHookTypes, "afterRender", AbstractClientPlayerEntity.class, float.class, float.class, MatrixStack.class, IRenderTypeBuffer.class, int.class);

        addMethod(id, baseClass, beforeRenderItemHookTypes, "beforeRenderItem", MatrixStack.class, IRenderTypeBuffer.class, int.class, AbstractClientPlayerEntity.class, ModelRenderer.class, ModelRenderer.class);
        addMethod(id, baseClass, overrideRenderItemHookTypes, "renderItem", MatrixStack.class, IRenderTypeBuffer.class, int.class, AbstractClientPlayerEntity.class, ModelRenderer.class, ModelRenderer.class);
        addMethod(id, baseClass, afterRenderItemHookTypes, "afterRenderItem", MatrixStack.class, IRenderTypeBuffer.class, int.class, AbstractClientPlayerEntity.class, ModelRenderer.class, ModelRenderer.class);

        addMethod(id, baseClass, beforeRenderLeftArmHookTypes, "beforeRenderLeftArm", MatrixStack.class, IRenderTypeBuffer.class, int.class, AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, overrideRenderLeftArmHookTypes, "renderLeftArm", MatrixStack.class, IRenderTypeBuffer.class, int.class, AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, afterRenderLeftArmHookTypes, "afterRenderLeftArm", MatrixStack.class, IRenderTypeBuffer.class, int.class, AbstractClientPlayerEntity.class);

        addMethod(id, baseClass, beforeRenderNameHookTypes, "beforeRenderName", AbstractClientPlayerEntity.class, String.class, MatrixStack.class, IRenderTypeBuffer.class, int.class);
        addMethod(id, baseClass, overrideRenderNameHookTypes, "renderName", AbstractClientPlayerEntity.class, String.class, MatrixStack.class, IRenderTypeBuffer.class, int.class);
        addMethod(id, baseClass, afterRenderNameHookTypes, "afterRenderName", AbstractClientPlayerEntity.class, String.class, MatrixStack.class, IRenderTypeBuffer.class, int.class);

        addMethod(id, baseClass, beforeRenderRightArmHookTypes, "beforeRenderRightArm", MatrixStack.class, IRenderTypeBuffer.class, int.class, AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, overrideRenderRightArmHookTypes, "renderRightArm", MatrixStack.class, IRenderTypeBuffer.class, int.class, AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, afterRenderRightArmHookTypes, "afterRenderRightArm", MatrixStack.class, IRenderTypeBuffer.class, int.class, AbstractClientPlayerEntity.class);

        addMethod(id, baseClass, beforeSetModelVisibilitiesHookTypes, "beforeSetModelVisibilities", AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, overrideSetModelVisibilitiesHookTypes, "setModelVisibilities", AbstractClientPlayerEntity.class);
        addMethod(id, baseClass, afterSetModelVisibilitiesHookTypes, "afterSetModelVisibilities", AbstractClientPlayerEntity.class);

        addMethod(id, baseClass, beforeShouldRenderHookTypes, "beforeShouldRender", AbstractClientPlayerEntity.class, ClippingHelperImpl.class, double.class, double.class, double.class);
        addMethod(id, baseClass, overrideShouldRenderHookTypes, "shouldRender", AbstractClientPlayerEntity.class, ClippingHelperImpl.class, double.class, double.class, double.class);
        addMethod(id, baseClass, afterShouldRenderHookTypes, "afterShouldRender", AbstractClientPlayerEntity.class, ClippingHelperImpl.class, double.class, double.class, double.class);

        addDynamicMethods(id, baseClass);

        addDynamicKeys(id, baseClass, beforeDynamicHookMethods, beforeDynamicHookTypes);
        addDynamicKeys(id, baseClass, overrideDynamicHookMethods, overrideDynamicHookTypes);
        addDynamicKeys(id, baseClass, afterDynamicHookMethods, afterDynamicHookTypes);

        initialize();

        for (IPlayerRenderer instance : getAllInstancesList()) {
            instance.getPlayerRendererAPI().attachPlayerRendererBase(id);
        }

        System.out.println("Render Player: registered " + id);
        logger.fine("Render Player: registered class '" + baseClass.getName() + "' with id '" + id + "'");

        initialized = false;
    }

    public static boolean unregister(String id)
    {
        if (id == null) {
            return false;
        }

        Constructor<?> constructor = allBaseConstructors.remove(id);
        if (constructor == null) {
            return false;
        }

        for (IPlayerRenderer instance : getAllInstancesList()) {
            instance.getPlayerRendererAPI().detachPlayerRendererBase(id);
        }

        beforeLocalConstructingHookTypes.remove(id);
        afterLocalConstructingHookTypes.remove(id);

        allBaseBeforeApplyRotationsSuperiors.remove(id);
        allBaseBeforeApplyRotationsInferiors.remove(id);
        allBaseOverrideApplyRotationsSuperiors.remove(id);
        allBaseOverrideApplyRotationsInferiors.remove(id);
        allBaseAfterApplyRotationsSuperiors.remove(id);
        allBaseAfterApplyRotationsInferiors.remove(id);

        beforeApplyRotationsHookTypes.remove(id);
        overrideApplyRotationsHookTypes.remove(id);
        afterApplyRotationsHookTypes.remove(id);

        allBaseBeforeCanRenderNameSuperiors.remove(id);
        allBaseBeforeCanRenderNameInferiors.remove(id);
        allBaseOverrideCanRenderNameSuperiors.remove(id);
        allBaseOverrideCanRenderNameInferiors.remove(id);
        allBaseAfterCanRenderNameSuperiors.remove(id);
        allBaseAfterCanRenderNameInferiors.remove(id);

        beforeCanRenderNameHookTypes.remove(id);
        overrideCanRenderNameHookTypes.remove(id);
        afterCanRenderNameHookTypes.remove(id);

        allBaseBeforeGetArmPoseSuperiors.remove(id);
        allBaseBeforeGetArmPoseInferiors.remove(id);
        allBaseOverrideGetArmPoseSuperiors.remove(id);
        allBaseOverrideGetArmPoseInferiors.remove(id);
        allBaseAfterGetArmPoseSuperiors.remove(id);
        allBaseAfterGetArmPoseInferiors.remove(id);

        beforeGetArmPoseHookTypes.remove(id);
        overrideGetArmPoseHookTypes.remove(id);
        afterGetArmPoseHookTypes.remove(id);

        allBaseBeforeGetDeathMaxRotationSuperiors.remove(id);
        allBaseBeforeGetDeathMaxRotationInferiors.remove(id);
        allBaseOverrideGetDeathMaxRotationSuperiors.remove(id);
        allBaseOverrideGetDeathMaxRotationInferiors.remove(id);
        allBaseAfterGetDeathMaxRotationSuperiors.remove(id);
        allBaseAfterGetDeathMaxRotationInferiors.remove(id);

        beforeGetDeathMaxRotationHookTypes.remove(id);
        overrideGetDeathMaxRotationHookTypes.remove(id);
        afterGetDeathMaxRotationHookTypes.remove(id);

        allBaseBeforeGetEntityModelSuperiors.remove(id);
        allBaseBeforeGetEntityModelInferiors.remove(id);
        allBaseOverrideGetEntityModelSuperiors.remove(id);
        allBaseOverrideGetEntityModelInferiors.remove(id);
        allBaseAfterGetEntityModelSuperiors.remove(id);
        allBaseAfterGetEntityModelInferiors.remove(id);

        beforeGetEntityModelHookTypes.remove(id);
        overrideGetEntityModelHookTypes.remove(id);
        afterGetEntityModelHookTypes.remove(id);

        allBaseBeforeGetEntityTextureSuperiors.remove(id);
        allBaseBeforeGetEntityTextureInferiors.remove(id);
        allBaseOverrideGetEntityTextureSuperiors.remove(id);
        allBaseOverrideGetEntityTextureInferiors.remove(id);
        allBaseAfterGetEntityTextureSuperiors.remove(id);
        allBaseAfterGetEntityTextureInferiors.remove(id);

        beforeGetEntityTextureHookTypes.remove(id);
        overrideGetEntityTextureHookTypes.remove(id);
        afterGetEntityTextureHookTypes.remove(id);

        allBaseBeforeGetFontRendererFromRenderManagerSuperiors.remove(id);
        allBaseBeforeGetFontRendererFromRenderManagerInferiors.remove(id);
        allBaseOverrideGetFontRendererFromRenderManagerSuperiors.remove(id);
        allBaseOverrideGetFontRendererFromRenderManagerInferiors.remove(id);
        allBaseAfterGetFontRendererFromRenderManagerSuperiors.remove(id);
        allBaseAfterGetFontRendererFromRenderManagerInferiors.remove(id);

        beforeGetFontRendererFromRenderManagerHookTypes.remove(id);
        overrideGetFontRendererFromRenderManagerHookTypes.remove(id);
        afterGetFontRendererFromRenderManagerHookTypes.remove(id);

        allBaseBeforeGetRenderManagerSuperiors.remove(id);
        allBaseBeforeGetRenderManagerInferiors.remove(id);
        allBaseOverrideGetRenderManagerSuperiors.remove(id);
        allBaseOverrideGetRenderManagerInferiors.remove(id);
        allBaseAfterGetRenderManagerSuperiors.remove(id);
        allBaseAfterGetRenderManagerInferiors.remove(id);

        beforeGetRenderManagerHookTypes.remove(id);
        overrideGetRenderManagerHookTypes.remove(id);
        afterGetRenderManagerHookTypes.remove(id);

        allBaseBeforeGetRenderOffsetSuperiors.remove(id);
        allBaseBeforeGetRenderOffsetInferiors.remove(id);
        allBaseOverrideGetRenderOffsetSuperiors.remove(id);
        allBaseOverrideGetRenderOffsetInferiors.remove(id);
        allBaseAfterGetRenderOffsetSuperiors.remove(id);
        allBaseAfterGetRenderOffsetInferiors.remove(id);

        beforeGetRenderOffsetHookTypes.remove(id);
        overrideGetRenderOffsetHookTypes.remove(id);
        afterGetRenderOffsetHookTypes.remove(id);

        allBaseBeforeGetSwingProgressSuperiors.remove(id);
        allBaseBeforeGetSwingProgressInferiors.remove(id);
        allBaseOverrideGetSwingProgressSuperiors.remove(id);
        allBaseOverrideGetSwingProgressInferiors.remove(id);
        allBaseAfterGetSwingProgressSuperiors.remove(id);
        allBaseAfterGetSwingProgressInferiors.remove(id);

        beforeGetSwingProgressHookTypes.remove(id);
        overrideGetSwingProgressHookTypes.remove(id);
        afterGetSwingProgressHookTypes.remove(id);

        allBaseBeforeHandleRotationFloatSuperiors.remove(id);
        allBaseBeforeHandleRotationFloatInferiors.remove(id);
        allBaseOverrideHandleRotationFloatSuperiors.remove(id);
        allBaseOverrideHandleRotationFloatInferiors.remove(id);
        allBaseAfterHandleRotationFloatSuperiors.remove(id);
        allBaseAfterHandleRotationFloatInferiors.remove(id);

        beforeHandleRotationFloatHookTypes.remove(id);
        overrideHandleRotationFloatHookTypes.remove(id);
        afterHandleRotationFloatHookTypes.remove(id);

        allBaseBeforePreRenderCallbackSuperiors.remove(id);
        allBaseBeforePreRenderCallbackInferiors.remove(id);
        allBaseOverridePreRenderCallbackSuperiors.remove(id);
        allBaseOverridePreRenderCallbackInferiors.remove(id);
        allBaseAfterPreRenderCallbackSuperiors.remove(id);
        allBaseAfterPreRenderCallbackInferiors.remove(id);

        beforePreRenderCallbackHookTypes.remove(id);
        overridePreRenderCallbackHookTypes.remove(id);
        afterPreRenderCallbackHookTypes.remove(id);

        allBaseBeforeRenderSuperiors.remove(id);
        allBaseBeforeRenderInferiors.remove(id);
        allBaseOverrideRenderSuperiors.remove(id);
        allBaseOverrideRenderInferiors.remove(id);
        allBaseAfterRenderSuperiors.remove(id);
        allBaseAfterRenderInferiors.remove(id);

        beforeRenderHookTypes.remove(id);
        overrideRenderHookTypes.remove(id);
        afterRenderHookTypes.remove(id);

        allBaseBeforeRenderItemSuperiors.remove(id);
        allBaseBeforeRenderItemInferiors.remove(id);
        allBaseOverrideRenderItemSuperiors.remove(id);
        allBaseOverrideRenderItemInferiors.remove(id);
        allBaseAfterRenderItemSuperiors.remove(id);
        allBaseAfterRenderItemInferiors.remove(id);

        beforeRenderItemHookTypes.remove(id);
        overrideRenderItemHookTypes.remove(id);
        afterRenderItemHookTypes.remove(id);

        allBaseBeforeRenderLeftArmSuperiors.remove(id);
        allBaseBeforeRenderLeftArmInferiors.remove(id);
        allBaseOverrideRenderLeftArmSuperiors.remove(id);
        allBaseOverrideRenderLeftArmInferiors.remove(id);
        allBaseAfterRenderLeftArmSuperiors.remove(id);
        allBaseAfterRenderLeftArmInferiors.remove(id);

        beforeRenderLeftArmHookTypes.remove(id);
        overrideRenderLeftArmHookTypes.remove(id);
        afterRenderLeftArmHookTypes.remove(id);

        allBaseBeforeRenderNameSuperiors.remove(id);
        allBaseBeforeRenderNameInferiors.remove(id);
        allBaseOverrideRenderNameSuperiors.remove(id);
        allBaseOverrideRenderNameInferiors.remove(id);
        allBaseAfterRenderNameSuperiors.remove(id);
        allBaseAfterRenderNameInferiors.remove(id);

        beforeRenderNameHookTypes.remove(id);
        overrideRenderNameHookTypes.remove(id);
        afterRenderNameHookTypes.remove(id);

        allBaseBeforeRenderRightArmSuperiors.remove(id);
        allBaseBeforeRenderRightArmInferiors.remove(id);
        allBaseOverrideRenderRightArmSuperiors.remove(id);
        allBaseOverrideRenderRightArmInferiors.remove(id);
        allBaseAfterRenderRightArmSuperiors.remove(id);
        allBaseAfterRenderRightArmInferiors.remove(id);

        beforeRenderRightArmHookTypes.remove(id);
        overrideRenderRightArmHookTypes.remove(id);
        afterRenderRightArmHookTypes.remove(id);

        allBaseBeforeSetModelVisibilitiesSuperiors.remove(id);
        allBaseBeforeSetModelVisibilitiesInferiors.remove(id);
        allBaseOverrideSetModelVisibilitiesSuperiors.remove(id);
        allBaseOverrideSetModelVisibilitiesInferiors.remove(id);
        allBaseAfterSetModelVisibilitiesSuperiors.remove(id);
        allBaseAfterSetModelVisibilitiesInferiors.remove(id);

        beforeSetModelVisibilitiesHookTypes.remove(id);
        overrideSetModelVisibilitiesHookTypes.remove(id);
        afterSetModelVisibilitiesHookTypes.remove(id);

        allBaseBeforeShouldRenderSuperiors.remove(id);
        allBaseBeforeShouldRenderInferiors.remove(id);
        allBaseOverrideShouldRenderSuperiors.remove(id);
        allBaseOverrideShouldRenderInferiors.remove(id);
        allBaseAfterShouldRenderSuperiors.remove(id);
        allBaseAfterShouldRenderInferiors.remove(id);

        beforeShouldRenderHookTypes.remove(id);
        overrideShouldRenderHookTypes.remove(id);
        afterShouldRenderHookTypes.remove(id);

        for (IPlayerRenderer instance : getAllInstancesList()) {
            instance.getPlayerRendererAPI().updatePlayerRendererBases();
        }

        Iterator<String> iterator = keysToVirtualIds.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (keysToVirtualIds.get(key).equals(id)) {
                keysToVirtualIds.remove(key);
            }
        }

        boolean otherFound = false;
        Class<?> type = constructor.getDeclaringClass();

        iterator = allBaseConstructors.keySet().iterator();
        while (iterator.hasNext()) {
            String otherId = iterator.next();
            Class<?> otherType = allBaseConstructors.get(otherId).getDeclaringClass();
            if (!otherId.equals(id) && otherType.equals(type)) {
                otherFound = true;
                break;
            }
        }

        if (!otherFound) {
            dynamicTypes.remove(type);

            virtualDynamicHookMethods.remove(type);

            beforeDynamicHookMethods.remove(type);
            overrideDynamicHookMethods.remove(type);
            afterDynamicHookMethods.remove(type);
        }

        removeDynamicHookTypes(id, beforeDynamicHookTypes);
        removeDynamicHookTypes(id, overrideDynamicHookTypes);
        removeDynamicHookTypes(id, afterDynamicHookTypes);

        allBaseBeforeDynamicSuperiors.remove(id);
        allBaseBeforeDynamicInferiors.remove(id);
        allBaseOverrideDynamicSuperiors.remove(id);
        allBaseOverrideDynamicInferiors.remove(id);
        allBaseAfterDynamicSuperiors.remove(id);
        allBaseAfterDynamicInferiors.remove(id);

        log("PlayerRendererAPI: unregistered id '" + id + "'");

        return true;
    }

    public static void removeDynamicHookTypes(String id, Map<String, List<String>> map)
    {
        for (String s : map.keySet()) {
            map.get(s).remove(id);
        }
    }

    public static Set<String> getRegisteredIds()
    {
        return unmodifiableAllIds;
    }

    private static void addSorting(String id, Map<String, String[]> map, String[] values)
    {
        if (values != null && values.length > 0) {
            map.put(id, values);
        }
    }

    private static void addDynamicSorting(String id, Map<String, Map<String, String[]>> map, Map<String, String[]> values)
    {
        if (values != null && values.size() > 0) {
            map.put(id, values);
        }
    }

    private static void addMethod(String id, Class<?> baseClass, List<String> list, String methodName, Class<?>... _parameterTypes)
    {
        try {
            Method method = baseClass.getMethod(methodName, _parameterTypes);
            boolean isOverridden = method.getDeclaringClass() != PlayerRendererBase.class;
            if (isOverridden) {
                list.add(id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Can not reflect method '" + methodName + "' of class '" + baseClass.getName() + "'", e);
        }
    }

    private static void addDynamicMethods(String id, Class<?> baseClass)
    {
        if (!dynamicTypes.add(baseClass)) {
            return;
        }

        Map<String, Method> virtuals = null;
        Map<String, Method> befores = null;
        Map<String, Method> overrides = null;
        Map<String, Method> afters = null;

        Method[] methods = baseClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass() != baseClass) {
                continue;
            }

            int modifiers = method.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                continue;
            }

            if (Modifier.isStatic(modifiers)) {
                continue;
            }

            String name = method.getName();
            if (name.length() < 7 || !name.substring(0, 7).equalsIgnoreCase("dynamic")) {
                continue;
            } else {
                name = name.substring(7);
            }

            while (name.charAt(0) == '_') {
                name = name.substring(1);
            }

            boolean before = false;
            boolean virtual = false;
            boolean override = false;
            boolean after = false;

            if (name.substring(0, 7).equalsIgnoreCase("virtual")) {
                virtual = true;
                name = name.substring(7);
            } else {
                if (name.length() >= 8 && name.substring(0, 8).equalsIgnoreCase("override")) {
                    name = name.substring(8);
                    override = true;
                } else if (name.substring(0, 6).equalsIgnoreCase("before")) {
                    before = true;
                    name = name.substring(6);
                } else if (name.substring(0, 5).equalsIgnoreCase("after")) {
                    after = true;
                    name = name.substring(5);
                }
            }

            if (name.length() >= 1 && (before || virtual || override || after)) {
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
            }

            while (name.charAt(0) == '_') {
                name = name.substring(1);
            }

            keys.add(name);

            if (virtual) {
                if (keysToVirtualIds.containsKey(name)) {
                    throw new RuntimeException("Can not process more than one dynamic virtual method");
                }

                keysToVirtualIds.put(name, id);
                virtuals = addDynamicMethod(name, method, virtuals);
            } else if (before) {
                befores = addDynamicMethod(name, method, befores);
            } else if (after) {
                afters = addDynamicMethod(name, method, afters);
            } else {
                overrides = addDynamicMethod(name, method, overrides);
            }
        }

        if (virtuals != null) {
            virtualDynamicHookMethods.put(baseClass, virtuals);
        }
        if (befores != null) {
            beforeDynamicHookMethods.put(baseClass, befores);
        }
        if (overrides != null) {
            overrideDynamicHookMethods.put(baseClass, overrides);
        }
        if (afters != null) {
            afterDynamicHookMethods.put(baseClass, afters);
        }
    }

    private static void addDynamicKeys(String id, Class<?> baseClass, Map<Class<?>, Map<String, Method>> dynamicHookMethods, Map<String, List<String>> dynamicHookTypes)
    {
        Map<String, Method> methods = dynamicHookMethods.get(baseClass);
        if (methods == null || methods.size() == 0) {
            return;
        }

        for (String key : methods.keySet()) {
            if (!dynamicHookTypes.containsKey(key)) {
                dynamicHookTypes.put(key, new ArrayList<>(1));
            }
            dynamicHookTypes.get(key).add(id);
        }
    }

    private static Map<String, Method> addDynamicMethod(String key, Method method, Map<String, Method> methods)
    {
        if (methods == null) {
            methods = new HashMap<>();
        }
        if (methods.containsKey(key)) {
            throw new RuntimeException("method with key '" + key + "' already exists");
        }
        methods.put(key, method);
        return methods;
    }

    public static PlayerRendererAPI create(IPlayerRenderer renderPlayer)
    {
        if (allBaseConstructors.size() > 0 && !initialized) {
            initialize();
        }
        return new PlayerRendererAPI(renderPlayer);
    }

    private static void initialize()
    {
        sortBases(beforeLocalConstructingHookTypes, allBaseBeforeLocalConstructingSuperiors, allBaseBeforeLocalConstructingInferiors, "beforeLocalConstructing");
        sortBases(afterLocalConstructingHookTypes, allBaseAfterLocalConstructingSuperiors, allBaseAfterLocalConstructingInferiors, "afterLocalConstructing");

        for (String key : keys) {
            sortDynamicBases(beforeDynamicHookTypes, allBaseBeforeDynamicSuperiors, allBaseBeforeDynamicInferiors, key);
            sortDynamicBases(overrideDynamicHookTypes, allBaseOverrideDynamicSuperiors, allBaseOverrideDynamicInferiors, key);
            sortDynamicBases(afterDynamicHookTypes, allBaseAfterDynamicSuperiors, allBaseAfterDynamicInferiors, key);
        }

        sortBases(beforeApplyRotationsHookTypes, allBaseBeforeApplyRotationsSuperiors, allBaseBeforeApplyRotationsInferiors, "beforeApplyRotations");
        sortBases(overrideApplyRotationsHookTypes, allBaseOverrideApplyRotationsSuperiors, allBaseOverrideApplyRotationsInferiors, "overrideApplyRotations");
        sortBases(afterApplyRotationsHookTypes, allBaseAfterApplyRotationsSuperiors, allBaseAfterApplyRotationsInferiors, "afterApplyRotations");

        sortBases(beforeCanRenderNameHookTypes, allBaseBeforeCanRenderNameSuperiors, allBaseBeforeCanRenderNameInferiors, "beforeCanRenderName");
        sortBases(overrideCanRenderNameHookTypes, allBaseOverrideCanRenderNameSuperiors, allBaseOverrideCanRenderNameInferiors, "overrideCanRenderName");
        sortBases(afterCanRenderNameHookTypes, allBaseAfterCanRenderNameSuperiors, allBaseAfterCanRenderNameInferiors, "afterCanRenderName");

        sortBases(beforeGetArmPoseHookTypes, allBaseBeforeGetArmPoseSuperiors, allBaseBeforeGetArmPoseInferiors, "beforeGetArmPose");
        sortBases(overrideGetArmPoseHookTypes, allBaseOverrideGetArmPoseSuperiors, allBaseOverrideGetArmPoseInferiors, "overrideGetArmPose");
        sortBases(afterGetArmPoseHookTypes, allBaseAfterGetArmPoseSuperiors, allBaseAfterGetArmPoseInferiors, "afterGetArmPose");

        sortBases(beforeGetDeathMaxRotationHookTypes, allBaseBeforeGetDeathMaxRotationSuperiors, allBaseBeforeGetDeathMaxRotationInferiors, "beforeGetDeathMaxRotation");
        sortBases(overrideGetDeathMaxRotationHookTypes, allBaseOverrideGetDeathMaxRotationSuperiors, allBaseOverrideGetDeathMaxRotationInferiors, "overrideGetDeathMaxRotation");
        sortBases(afterGetDeathMaxRotationHookTypes, allBaseAfterGetDeathMaxRotationSuperiors, allBaseAfterGetDeathMaxRotationInferiors, "afterGetDeathMaxRotation");

        sortBases(beforeGetEntityModelHookTypes, allBaseBeforeGetEntityModelSuperiors, allBaseBeforeGetEntityModelInferiors, "beforeGetEntityModel");
        sortBases(overrideGetEntityModelHookTypes, allBaseOverrideGetEntityModelSuperiors, allBaseOverrideGetEntityModelInferiors, "overrideGetEntityModel");
        sortBases(afterGetEntityModelHookTypes, allBaseAfterGetEntityModelSuperiors, allBaseAfterGetEntityModelInferiors, "afterGetEntityModel");

        sortBases(beforeGetEntityTextureHookTypes, allBaseBeforeGetEntityTextureSuperiors, allBaseBeforeGetEntityTextureInferiors, "beforeGetEntityTexture");
        sortBases(overrideGetEntityTextureHookTypes, allBaseOverrideGetEntityTextureSuperiors, allBaseOverrideGetEntityTextureInferiors, "overrideGetEntityTexture");
        sortBases(afterGetEntityTextureHookTypes, allBaseAfterGetEntityTextureSuperiors, allBaseAfterGetEntityTextureInferiors, "afterGetEntityTexture");

        sortBases(beforeGetFontRendererFromRenderManagerHookTypes, allBaseBeforeGetFontRendererFromRenderManagerSuperiors, allBaseBeforeGetFontRendererFromRenderManagerInferiors, "beforeGetFontRendererFromRenderManager");
        sortBases(overrideGetFontRendererFromRenderManagerHookTypes, allBaseOverrideGetFontRendererFromRenderManagerSuperiors, allBaseOverrideGetFontRendererFromRenderManagerInferiors, "overrideGetFontRendererFromRenderManager");
        sortBases(afterGetFontRendererFromRenderManagerHookTypes, allBaseAfterGetFontRendererFromRenderManagerSuperiors, allBaseAfterGetFontRendererFromRenderManagerInferiors, "afterGetFontRendererFromRenderManager");

        sortBases(beforeGetRenderManagerHookTypes, allBaseBeforeGetRenderManagerSuperiors, allBaseBeforeGetRenderManagerInferiors, "beforeGetRenderManager");
        sortBases(overrideGetRenderManagerHookTypes, allBaseOverrideGetRenderManagerSuperiors, allBaseOverrideGetRenderManagerInferiors, "overrideGetRenderManager");
        sortBases(afterGetRenderManagerHookTypes, allBaseAfterGetRenderManagerSuperiors, allBaseAfterGetRenderManagerInferiors, "afterGetRenderManager");

        sortBases(beforeGetRenderOffsetHookTypes, allBaseBeforeGetRenderOffsetSuperiors, allBaseBeforeGetRenderOffsetInferiors, "beforeGetRenderOffset");
        sortBases(overrideGetRenderOffsetHookTypes, allBaseOverrideGetRenderOffsetSuperiors, allBaseOverrideGetRenderOffsetInferiors, "overrideGetRenderOffset");
        sortBases(afterGetRenderOffsetHookTypes, allBaseAfterGetRenderOffsetSuperiors, allBaseAfterGetRenderOffsetInferiors, "afterGetRenderOffset");

        sortBases(beforeGetSwingProgressHookTypes, allBaseBeforeGetSwingProgressSuperiors, allBaseBeforeGetSwingProgressInferiors, "beforeGetSwingProgress");
        sortBases(overrideGetSwingProgressHookTypes, allBaseOverrideGetSwingProgressSuperiors, allBaseOverrideGetSwingProgressInferiors, "overrideGetSwingProgress");
        sortBases(afterGetSwingProgressHookTypes, allBaseAfterGetSwingProgressSuperiors, allBaseAfterGetSwingProgressInferiors, "afterGetSwingProgress");

        sortBases(beforeHandleRotationFloatHookTypes, allBaseBeforeHandleRotationFloatSuperiors, allBaseBeforeHandleRotationFloatInferiors, "beforeHandleRotationFloat");
        sortBases(overrideHandleRotationFloatHookTypes, allBaseOverrideHandleRotationFloatSuperiors, allBaseOverrideHandleRotationFloatInferiors, "overrideHandleRotationFloat");
        sortBases(afterHandleRotationFloatHookTypes, allBaseAfterHandleRotationFloatSuperiors, allBaseAfterHandleRotationFloatInferiors, "afterHandleRotationFloat");

        sortBases(beforePreRenderCallbackHookTypes, allBaseBeforePreRenderCallbackSuperiors, allBaseBeforePreRenderCallbackInferiors, "beforePreRenderCallback");
        sortBases(overridePreRenderCallbackHookTypes, allBaseOverridePreRenderCallbackSuperiors, allBaseOverridePreRenderCallbackInferiors, "overridePreRenderCallback");
        sortBases(afterPreRenderCallbackHookTypes, allBaseAfterPreRenderCallbackSuperiors, allBaseAfterPreRenderCallbackInferiors, "afterPreRenderCallback");

        sortBases(beforeRenderHookTypes, allBaseBeforeRenderSuperiors, allBaseBeforeRenderInferiors, "beforeRender");
        sortBases(overrideRenderHookTypes, allBaseOverrideRenderSuperiors, allBaseOverrideRenderInferiors, "overrideRender");
        sortBases(afterRenderHookTypes, allBaseAfterRenderSuperiors, allBaseAfterRenderInferiors, "afterRender");

        sortBases(beforeRenderItemHookTypes, allBaseBeforeRenderItemSuperiors, allBaseBeforeRenderItemInferiors, "beforeRenderItem");
        sortBases(overrideRenderItemHookTypes, allBaseOverrideRenderItemSuperiors, allBaseOverrideRenderItemInferiors, "overrideRenderItem");
        sortBases(afterRenderItemHookTypes, allBaseAfterRenderItemSuperiors, allBaseAfterRenderItemInferiors, "afterRenderItem");

        sortBases(beforeRenderLeftArmHookTypes, allBaseBeforeRenderLeftArmSuperiors, allBaseBeforeRenderLeftArmInferiors, "beforeRenderLeftArm");
        sortBases(overrideRenderLeftArmHookTypes, allBaseOverrideRenderLeftArmSuperiors, allBaseOverrideRenderLeftArmInferiors, "overrideRenderLeftArm");
        sortBases(afterRenderLeftArmHookTypes, allBaseAfterRenderLeftArmSuperiors, allBaseAfterRenderLeftArmInferiors, "afterRenderLeftArm");

        sortBases(beforeRenderNameHookTypes, allBaseBeforeRenderNameSuperiors, allBaseBeforeRenderNameInferiors, "beforeRenderName");
        sortBases(overrideRenderNameHookTypes, allBaseOverrideRenderNameSuperiors, allBaseOverrideRenderNameInferiors, "overrideRenderName");
        sortBases(afterRenderNameHookTypes, allBaseAfterRenderNameSuperiors, allBaseAfterRenderNameInferiors, "afterRenderName");

        sortBases(beforeRenderRightArmHookTypes, allBaseBeforeRenderRightArmSuperiors, allBaseBeforeRenderRightArmInferiors, "beforeRenderRightArm");
        sortBases(overrideRenderRightArmHookTypes, allBaseOverrideRenderRightArmSuperiors, allBaseOverrideRenderRightArmInferiors, "overrideRenderRightArm");
        sortBases(afterRenderRightArmHookTypes, allBaseAfterRenderRightArmSuperiors, allBaseAfterRenderRightArmInferiors, "afterRenderRightArm");

        sortBases(beforeSetModelVisibilitiesHookTypes, allBaseBeforeSetModelVisibilitiesSuperiors, allBaseBeforeSetModelVisibilitiesInferiors, "beforeSetModelVisibilities");
        sortBases(overrideSetModelVisibilitiesHookTypes, allBaseOverrideSetModelVisibilitiesSuperiors, allBaseOverrideSetModelVisibilitiesInferiors, "overrideSetModelVisibilities");
        sortBases(afterSetModelVisibilitiesHookTypes, allBaseAfterSetModelVisibilitiesSuperiors, allBaseAfterSetModelVisibilitiesInferiors, "afterSetModelVisibilities");

        sortBases(beforeShouldRenderHookTypes, allBaseBeforeShouldRenderSuperiors, allBaseBeforeShouldRenderInferiors, "beforeShouldRender");
        sortBases(overrideShouldRenderHookTypes, allBaseOverrideShouldRenderSuperiors, allBaseOverrideShouldRenderInferiors, "overrideShouldRender");
        sortBases(afterShouldRenderHookTypes, allBaseAfterShouldRenderSuperiors, allBaseAfterShouldRenderInferiors, "afterShouldRender");

        initialized = true;
    }

    private static List<IPlayerRenderer> getAllInstancesList()
    {
        List<IPlayerRenderer> result = new ArrayList<>();
        for (Iterator<WeakReference<IPlayerRenderer>> iterator = allInstances.iterator(); iterator.hasNext(); ) {
            IPlayerRenderer instance = iterator.next().get();
            if (instance != null) {
                result.add(instance);
            } else {
                iterator.remove();
            }
        }
        return result;
    }

    private static final List<WeakReference<IPlayerRenderer>> allInstances = new ArrayList<>();

    public static PlayerRenderer[] getAllInstances()
    {
        return getAllInstancesList().stream().map(instance -> (PlayerRenderer) instance).toArray(PlayerRenderer[]::new);
    }

    public static void beforeLocalConstructing(IPlayerRenderer renderPlayer, EntityRendererManager paramRenderManager, boolean paramBoolean)
    {
        PlayerRendererAPI playerRendererAPI = renderPlayer.getPlayerRendererAPI();
        if (playerRendererAPI != null) {
            playerRendererAPI.load();
        }

        allInstances.add(new WeakReference<>(renderPlayer));

        if (playerRendererAPI != null) {
            playerRendererAPI.beforeLocalConstructing(paramRenderManager, paramBoolean);
        }
    }

    public static void afterLocalConstructing(IPlayerRenderer renderPlayer, EntityRendererManager paramRenderManager, boolean paramBoolean)
    {
        PlayerRendererAPI playerRendererAPI = renderPlayer.getPlayerRendererAPI();
        if (playerRendererAPI != null) {
            playerRendererAPI.afterLocalConstructing(paramRenderManager, paramBoolean);
        }
    }

    public static PlayerRendererBase getPlayerRendererBase(IPlayerRenderer renderPlayer, String baseId)
    {
        PlayerRendererAPI playerRendererAPI = renderPlayer.getPlayerRendererAPI();
        if (playerRendererAPI != null) {
            return playerRendererAPI.getPlayerRendererBase(baseId);
        }
        return null;
    }

    public static Set<String> getPlayerRendererBaseIds(IPlayerRenderer renderPlayer)
    {
        PlayerRendererAPI playerRendererAPI = renderPlayer.getPlayerRendererAPI();
        Set<String> result;
        if (playerRendererAPI != null) {
            result = playerRendererAPI.getPlayerRendererBaseIds();
        } else {
            result = Collections.emptySet();
        }
        return result;
    }

    public static Object dynamic(IPlayerRenderer renderPlayer, String key, Object[] parameters)
    {
        PlayerRendererAPI playerRendererAPI = renderPlayer.getPlayerRendererAPI();
        if (playerRendererAPI != null) {
            return playerRendererAPI.dynamic(key, parameters);
        }
        return null;
    }

    private static void sortBases(List<String> list, Map<String, String[]> allBaseSuperiors, Map<String, String[]> allBaseInferiors, String methodName)
    {
        new PlayerRendererBaseSorter(list, allBaseSuperiors, allBaseInferiors, methodName).Sort();
    }

    private final static Map<String, String[]> EmptySortMap = Collections.unmodifiableMap(new HashMap<>());

    private static void sortDynamicBases(Map<String, List<String>> lists, Map<String, Map<String, String[]>> allBaseSuperiors, Map<String, Map<String, String[]>> allBaseInferiors, String key)
    {
        List<String> types = lists.get(key);
        if (types != null && types.size() > 1) {
            sortBases(types, getDynamicSorters(key, types, allBaseSuperiors), getDynamicSorters(key, types, allBaseInferiors), key);
        }
    }

    private static Map<String, String[]> getDynamicSorters(String key, List<String> toSort, Map<String, Map<String, String[]>> allBaseValues)
    {
        Map<String, String[]> superiors = null;

        for (String id : toSort) {
            Map<String, String[]> idSuperiors = allBaseValues.get(id);
            if (idSuperiors == null) {
                continue;
            }

            String[] keySuperiorIds = idSuperiors.get(key);
            if (keySuperiorIds != null && keySuperiorIds.length > 0) {
                if (superiors == null) {
                    superiors = new HashMap<>(1);
                }
                superiors.put(id, keySuperiorIds);
            }
        }

        return superiors != null ? superiors : EmptySortMap;
    }

    private PlayerRendererAPI(IPlayerRenderer iPlayerRenderer)
    {
        this.iPlayerRenderer = iPlayerRenderer;
    }

    private void load()
    {
        Iterator<String> iterator = allBaseConstructors.keySet().iterator();
        while (iterator.hasNext()) {
            String id = iterator.next();
            PlayerRendererBase toAttach = this.createPlayerRendererBase(id);
            toAttach.beforeBaseAttach(false);
            this.allBaseObjects.put(id, toAttach);
            this.baseObjectsToId.put(toAttach, id);
        }

        this.beforeLocalConstructingHooks = this.create(beforeLocalConstructingHookTypes);
        this.afterLocalConstructingHooks = this.create(afterLocalConstructingHookTypes);

        this.updatePlayerRendererBases();

        iterator = this.allBaseObjects.keySet().iterator();
        while (iterator.hasNext()) {
            this.allBaseObjects.get(iterator.next()).afterBaseAttach(false);
        }
    }

    private PlayerRendererBase createPlayerRendererBase(String id)
    {
        Constructor<?> constructor = allBaseConstructors.get(id);

        PlayerRendererBase base;
        try {
            if (constructor.getParameterTypes().length == 1) {
                base = (PlayerRendererBase) constructor.newInstance(this);
            } else {
                base = (PlayerRendererBase) constructor.newInstance(this, id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception while creating a PlayerRendererBase of type '" + constructor.getDeclaringClass() + "'", e);
        }
        return base;
    }

    private void updatePlayerRendererBases()
    {
        this.beforeApplyRotationsHooks = this.create(beforeApplyRotationsHookTypes);
        this.overrideApplyRotationsHooks = this.create(overrideApplyRotationsHookTypes);
        this.afterApplyRotationsHooks = this.create(afterApplyRotationsHookTypes);
        this.isApplyRotationsModded =
                this.beforeApplyRotationsHooks != null ||
                        this.overrideApplyRotationsHooks != null ||
                        this.afterApplyRotationsHooks != null;

        this.beforeCanRenderNameHooks = this.create(beforeCanRenderNameHookTypes);
        this.overrideCanRenderNameHooks = this.create(overrideCanRenderNameHookTypes);
        this.afterCanRenderNameHooks = this.create(afterCanRenderNameHookTypes);
        this.isCanRenderNameModded =
                this.beforeCanRenderNameHooks != null ||
                        this.overrideCanRenderNameHooks != null ||
                        this.afterCanRenderNameHooks != null;

        this.beforeGetArmPoseHooks = this.create(beforeGetArmPoseHookTypes);
        this.overrideGetArmPoseHooks = this.create(overrideGetArmPoseHookTypes);
        this.afterGetArmPoseHooks = this.create(afterGetArmPoseHookTypes);
        this.isGetArmPoseModded =
                this.beforeGetArmPoseHooks != null ||
                        this.overrideGetArmPoseHooks != null ||
                        this.afterGetArmPoseHooks != null;

        this.beforeGetDeathMaxRotationHooks = this.create(beforeGetDeathMaxRotationHookTypes);
        this.overrideGetDeathMaxRotationHooks = this.create(overrideGetDeathMaxRotationHookTypes);
        this.afterGetDeathMaxRotationHooks = this.create(afterGetDeathMaxRotationHookTypes);
        this.isGetDeathMaxRotationModded =
                this.beforeGetDeathMaxRotationHooks != null ||
                        this.overrideGetDeathMaxRotationHooks != null ||
                        this.afterGetDeathMaxRotationHooks != null;

        this.beforeGetEntityModelHooks = this.create(beforeGetEntityModelHookTypes);
        this.overrideGetEntityModelHooks = this.create(overrideGetEntityModelHookTypes);
        this.afterGetEntityModelHooks = this.create(afterGetEntityModelHookTypes);
        this.isGetEntityModelModded =
                this.beforeGetEntityModelHooks != null ||
                        this.overrideGetEntityModelHooks != null ||
                        this.afterGetEntityModelHooks != null;

        this.beforeGetEntityTextureHooks = this.create(beforeGetEntityTextureHookTypes);
        this.overrideGetEntityTextureHooks = this.create(overrideGetEntityTextureHookTypes);
        this.afterGetEntityTextureHooks = this.create(afterGetEntityTextureHookTypes);
        this.isGetEntityTextureModded =
                this.beforeGetEntityTextureHooks != null ||
                        this.overrideGetEntityTextureHooks != null ||
                        this.afterGetEntityTextureHooks != null;

        this.beforeGetFontRendererFromRenderManagerHooks = this.create(beforeGetFontRendererFromRenderManagerHookTypes);
        this.overrideGetFontRendererFromRenderManagerHooks = this.create(overrideGetFontRendererFromRenderManagerHookTypes);
        this.afterGetFontRendererFromRenderManagerHooks = this.create(afterGetFontRendererFromRenderManagerHookTypes);
        this.isGetFontRendererFromRenderManagerModded =
                this.beforeGetFontRendererFromRenderManagerHooks != null ||
                        this.overrideGetFontRendererFromRenderManagerHooks != null ||
                        this.afterGetFontRendererFromRenderManagerHooks != null;

        this.beforeGetRenderManagerHooks = this.create(beforeGetRenderManagerHookTypes);
        this.overrideGetRenderManagerHooks = this.create(overrideGetRenderManagerHookTypes);
        this.afterGetRenderManagerHooks = this.create(afterGetRenderManagerHookTypes);
        this.isGetRenderManagerModded =
                this.beforeGetRenderManagerHooks != null ||
                        this.overrideGetRenderManagerHooks != null ||
                        this.afterGetRenderManagerHooks != null;

        this.beforeGetRenderOffsetHooks = this.create(beforeGetRenderOffsetHookTypes);
        this.overrideGetRenderOffsetHooks = this.create(overrideGetRenderOffsetHookTypes);
        this.afterGetRenderOffsetHooks = this.create(afterGetRenderOffsetHookTypes);
        this.isGetRenderOffsetModded =
                this.beforeGetRenderOffsetHooks != null ||
                        this.overrideGetRenderOffsetHooks != null ||
                        this.afterGetRenderOffsetHooks != null;

        this.beforeGetSwingProgressHooks = this.create(beforeGetSwingProgressHookTypes);
        this.overrideGetSwingProgressHooks = this.create(overrideGetSwingProgressHookTypes);
        this.afterGetSwingProgressHooks = this.create(afterGetSwingProgressHookTypes);
        this.isGetSwingProgressModded =
                this.beforeGetSwingProgressHooks != null ||
                        this.overrideGetSwingProgressHooks != null ||
                        this.afterGetSwingProgressHooks != null;

        this.beforeHandleRotationFloatHooks = this.create(beforeHandleRotationFloatHookTypes);
        this.overrideHandleRotationFloatHooks = this.create(overrideHandleRotationFloatHookTypes);
        this.afterHandleRotationFloatHooks = this.create(afterHandleRotationFloatHookTypes);
        this.isHandleRotationFloatModded =
                this.beforeHandleRotationFloatHooks != null ||
                        this.overrideHandleRotationFloatHooks != null ||
                        this.afterHandleRotationFloatHooks != null;

        this.beforePreRenderCallbackHooks = this.create(beforePreRenderCallbackHookTypes);
        this.overridePreRenderCallbackHooks = this.create(overridePreRenderCallbackHookTypes);
        this.afterPreRenderCallbackHooks = this.create(afterPreRenderCallbackHookTypes);
        this.isPreRenderCallbackModded =
                this.beforePreRenderCallbackHooks != null ||
                        this.overridePreRenderCallbackHooks != null ||
                        this.afterPreRenderCallbackHooks != null;

        this.beforeRenderHooks = this.create(beforeRenderHookTypes);
        this.overrideRenderHooks = this.create(overrideRenderHookTypes);
        this.afterRenderHooks = this.create(afterRenderHookTypes);
        this.isRenderModded =
                this.beforeRenderHooks != null ||
                        this.overrideRenderHooks != null ||
                        this.afterRenderHooks != null;

        this.beforeRenderItemHooks = this.create(beforeRenderItemHookTypes);
        this.overrideRenderItemHooks = this.create(overrideRenderItemHookTypes);
        this.afterRenderItemHooks = this.create(afterRenderItemHookTypes);
        this.isRenderItemModded =
                this.beforeRenderItemHooks != null ||
                        this.overrideRenderItemHooks != null ||
                        this.afterRenderItemHooks != null;

        this.beforeRenderLeftArmHooks = this.create(beforeRenderLeftArmHookTypes);
        this.overrideRenderLeftArmHooks = this.create(overrideRenderLeftArmHookTypes);
        this.afterRenderLeftArmHooks = this.create(afterRenderLeftArmHookTypes);
        this.isRenderLeftArmModded =
                this.beforeRenderLeftArmHooks != null ||
                        this.overrideRenderLeftArmHooks != null ||
                        this.afterRenderLeftArmHooks != null;

        this.beforeRenderNameHooks = this.create(beforeRenderNameHookTypes);
        this.overrideRenderNameHooks = this.create(overrideRenderNameHookTypes);
        this.afterRenderNameHooks = this.create(afterRenderNameHookTypes);
        this.isRenderNameModded =
                this.beforeRenderNameHooks != null ||
                        this.overrideRenderNameHooks != null ||
                        this.afterRenderNameHooks != null;

        this.beforeRenderRightArmHooks = this.create(beforeRenderRightArmHookTypes);
        this.overrideRenderRightArmHooks = this.create(overrideRenderRightArmHookTypes);
        this.afterRenderRightArmHooks = this.create(afterRenderRightArmHookTypes);
        this.isRenderRightArmModded =
                this.beforeRenderRightArmHooks != null ||
                        this.overrideRenderRightArmHooks != null ||
                        this.afterRenderRightArmHooks != null;

        this.beforeSetModelVisibilitiesHooks = this.create(beforeSetModelVisibilitiesHookTypes);
        this.overrideSetModelVisibilitiesHooks = this.create(overrideSetModelVisibilitiesHookTypes);
        this.afterSetModelVisibilitiesHooks = this.create(afterSetModelVisibilitiesHookTypes);
        this.isSetModelVisibilitiesModded =
                this.beforeSetModelVisibilitiesHooks != null ||
                        this.overrideSetModelVisibilitiesHooks != null ||
                        this.afterSetModelVisibilitiesHooks != null;

        this.beforeShouldRenderHooks = this.create(beforeShouldRenderHookTypes);
        this.overrideShouldRenderHooks = this.create(overrideShouldRenderHookTypes);
        this.afterShouldRenderHooks = this.create(afterShouldRenderHookTypes);
        this.isShouldRenderModded =
                this.beforeShouldRenderHooks != null ||
                        this.overrideShouldRenderHooks != null ||
                        this.afterShouldRenderHooks != null;
    }

    private void attachPlayerRendererBase(String id)
    {
        PlayerRendererBase toAttach = this.createPlayerRendererBase(id);
        toAttach.beforeBaseAttach(true);
        this.allBaseObjects.put(id, toAttach);
        this.updatePlayerRendererBases();
        toAttach.afterBaseAttach(true);
    }

    private void detachPlayerRendererBase(String id)
    {
        PlayerRendererBase toDetach = this.allBaseObjects.get(id);
        toDetach.beforeBaseDetach(true);
        this.allBaseObjects.remove(id);
        toDetach.afterBaseDetach(true);
    }

    private PlayerRendererBase[] create(List<String> types)
    {
        if (types.isEmpty()) {
            return null;
        }

        PlayerRendererBase[] result = new PlayerRendererBase[types.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = this.getPlayerRendererBase(types.get(i));
        }
        return result;
    }

    private void beforeLocalConstructing(EntityRendererManager paramRenderManager, boolean paramBoolean)
    {
        if (this.beforeLocalConstructingHooks != null) {
            for (int i = this.beforeLocalConstructingHooks.length - 1; i >= 0; i--) {
                this.beforeLocalConstructingHooks[i].beforeLocalConstructing(paramRenderManager, paramBoolean);
            }
        }
        this.beforeLocalConstructingHooks = null;
    }

    private void afterLocalConstructing(EntityRendererManager paramRenderManager, boolean paramBoolean)
    {
        if (this.afterLocalConstructingHooks != null) {
            for (PlayerRendererBase afterLocalConstructingHook : this.afterLocalConstructingHooks) {
                afterLocalConstructingHook.afterLocalConstructing(paramRenderManager, paramBoolean);
            }
        }
        this.afterLocalConstructingHooks = null;
    }

    public PlayerRendererBase getPlayerRendererBase(String id)
    {
        return this.allBaseObjects.get(id);
    }

    public Set<String> getPlayerRendererBaseIds()
    {
        return this.unmodifiableAllBaseIds;
    }

    public Object dynamic(String key, Object[] parameters)
    {
        key = key.replace('.', '_').replace(' ', '_');
        this.executeAll(key, parameters, beforeDynamicHookTypes, beforeDynamicHookMethods, true);
        Object result = this.dynamicOverwritten(key, parameters, null);
        this.executeAll(key, parameters, afterDynamicHookTypes, afterDynamicHookMethods, false);
        return result;
    }

    public Object dynamicOverwritten(String key, Object[] parameters, PlayerRendererBase overwriter)
    {
        List<String> overrideIds = overrideDynamicHookTypes.get(key);

        String id = null;
        if (overrideIds != null) {
            if (overwriter != null) {
                id = this.baseObjectsToId.get(overwriter);
                int index = overrideIds.indexOf(id);
                if (index > 0) {
                    id = overrideIds.get(index - 1);
                } else {
                    id = null;
                }
            } else if (overrideIds.size() > 0) {
                id = overrideIds.get(overrideIds.size() - 1);
            }
        }

        Map<Class<?>, Map<String, Method>> methodMap;

        if (id == null) {
            id = keysToVirtualIds.get(key);
            if (id == null) {
                return null;
            }
            methodMap = virtualDynamicHookMethods;
        } else {
            methodMap = overrideDynamicHookMethods;
        }

        Map<String, Method> methods = methodMap.get(allBaseConstructors.get(id).getDeclaringClass());
        if (methods == null) {
            return null;
        }

        Method method = methods.get(key);
        if (method == null) {
            return null;
        }

        return this.execute(this.getPlayerRendererBase(id), method, parameters);
    }

    private void executeAll(String key, Object[] parameters, Map<String, List<String>> dynamicHookTypes, Map<Class<?>, Map<String, Method>> dynamicHookMethods, boolean reverse)
    {
        List<String> beforeIds = dynamicHookTypes.get(key);
        if (beforeIds == null) {
            return;
        }

        for (int i = reverse ? beforeIds.size() - 1 : 0; reverse ? i >= 0 : i < beforeIds.size(); i = i + (reverse ? -1 : 1)) {
            String id = beforeIds.get(i);
            PlayerRendererBase base = this.getPlayerRendererBase(id);
            Class<?> type = base.getClass();

            Map<String, Method> methods = dynamicHookMethods.get(type);
            if (methods == null) {
                continue;
            }

            Method method = methods.get(key);
            if (method == null) {
                continue;
            }

            this.execute(base, method, parameters);
        }
    }

    private Object execute(PlayerRendererBase base, Method method, Object[] parameters)
    {
        try {
            return method.invoke(base, parameters);
        } catch (Exception e) {
            throw new RuntimeException("Exception while invoking dynamic method", e);
        }
    }

    // ############################################################################

    public static void beforeApplyRotations(CallbackInfo callbackInfo, IPlayerRenderer target, AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isApplyRotationsModded) {
            playerRendererAPI.beforeApplyRotations(callbackInfo, player, matrixStack, ageInTicks, rotationYaw, partialTicks);
        }
    }

    private void beforeApplyRotations(CallbackInfo callbackInfo, AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        if (this.beforeApplyRotationsHooks != null) {
            for (int i = this.beforeApplyRotationsHooks.length - 1; i >= 0; i--) {
                this.beforeApplyRotationsHooks[i].beforeApplyRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
            }
        }

        if (this.overrideApplyRotationsHooks != null) {
            this.overrideApplyRotationsHooks[this.overrideApplyRotationsHooks.length - 1].applyRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
            callbackInfo.cancel();
        }
    }

    public static void afterApplyRotations(IPlayerRenderer target, AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isApplyRotationsModded) {
            playerRendererAPI.afterApplyRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
        }
    }

    private void afterApplyRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float ageInTicks, float rotationYaw, float partialTicks)
    {
        if (this.afterApplyRotationsHooks != null) {
            for (PlayerRendererBase afterApplyRotationsHook : this.afterApplyRotationsHooks) {
                afterApplyRotationsHook.afterApplyRotations(player, matrixStack, ageInTicks, rotationYaw, partialTicks);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenApplyRotations(PlayerRendererBase overwriter)
    {
        if (this.overrideApplyRotationsHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideApplyRotationsHooks.length; i++) {
            if (this.overrideApplyRotationsHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideApplyRotationsHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeApplyRotationsHookTypes = new LinkedList<>();
    private final static List<String> overrideApplyRotationsHookTypes = new LinkedList<>();
    private final static List<String> afterApplyRotationsHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeApplyRotationsHooks;
    private PlayerRendererBase[] overrideApplyRotationsHooks;
    private PlayerRendererBase[] afterApplyRotationsHooks;
    public boolean isApplyRotationsModded;
    private static final Map<String, String[]> allBaseBeforeApplyRotationsSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeApplyRotationsInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideApplyRotationsSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideApplyRotationsInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterApplyRotationsSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterApplyRotationsInferiors = new Hashtable<>(0);

    // ############################################################################

    public static boolean canRenderName(IPlayerRenderer target, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isCanRenderNameModded) {
           return playerRendererAPI.canRenderName(player);
        } else {
           return target.superCanRenderName(player);
        }
    }

    private boolean canRenderName(AbstractClientPlayerEntity player)
    {
        if (this.beforeCanRenderNameHooks != null) {
            for (int i = this.beforeCanRenderNameHooks.length - 1; i >= 0; i--) {
                this.beforeCanRenderNameHooks[i].beforeCanRenderName(player);
            }
        }

        boolean result;
        if (this.overrideCanRenderNameHooks != null) {
            result = this.overrideCanRenderNameHooks[this.overrideCanRenderNameHooks.length - 1].canRenderName(player);
        } else {
            result = this.iPlayerRenderer.superCanRenderName(player);
        }

        if (this.afterCanRenderNameHooks != null) {
            for (PlayerRendererBase afterCanRenderNameHook : this.afterCanRenderNameHooks) {
                afterCanRenderNameHook.afterCanRenderName(player);
            }
        }

        return result;
    }

    protected PlayerRendererBase getOverwrittenCanRenderName(PlayerRendererBase overwriter)
    {
        if (this.overrideCanRenderNameHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideCanRenderNameHooks.length; i++) {
            if (this.overrideCanRenderNameHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideCanRenderNameHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeCanRenderNameHookTypes = new LinkedList<>();
    private final static List<String> overrideCanRenderNameHookTypes = new LinkedList<>();
    private final static List<String> afterCanRenderNameHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeCanRenderNameHooks;
    private PlayerRendererBase[] overrideCanRenderNameHooks;
    private PlayerRendererBase[] afterCanRenderNameHooks;
    public boolean isCanRenderNameModded;
    private static final Map<String, String[]> allBaseBeforeCanRenderNameSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeCanRenderNameInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideCanRenderNameSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideCanRenderNameInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterCanRenderNameSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterCanRenderNameInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforeGetArmPose(CallbackInfoReturnable<BipedModel.ArmPose> callbackInfo, IPlayerRenderer target, AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetArmPoseModded) {
            playerRendererAPI.beforeGetArmPose(callbackInfo, player, itemStackMain, itemStackOff, hand);
        }
    }

    private void beforeGetArmPose(CallbackInfoReturnable<BipedModel.ArmPose> callbackInfo, AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand)
    {
        if (this.beforeGetArmPoseHooks != null) {
            for (int i = this.beforeGetArmPoseHooks.length - 1; i >= 0; i--) {
                this.beforeGetArmPoseHooks[i].beforeGetArmPose(player, itemStackMain, itemStackOff, hand);
            }
        }

        if (this.overrideGetArmPoseHooks != null) {
            callbackInfo.setReturnValue(this.overrideGetArmPoseHooks[this.overrideGetArmPoseHooks.length - 1].getArmPose(player, itemStackMain, itemStackOff, hand));
            callbackInfo.cancel();
        }
    }

    public static void afterGetArmPose(IPlayerRenderer target, AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetArmPoseModded) {
            playerRendererAPI.afterGetArmPose(player, itemStackMain, itemStackOff, hand);
        }
    }

    private void afterGetArmPose(AbstractClientPlayerEntity player, ItemStack itemStackMain, ItemStack itemStackOff, Hand hand)
    {
        if (this.afterGetArmPoseHooks != null) {
            for (PlayerRendererBase afterGetArmPoseHook : this.afterGetArmPoseHooks) {
                afterGetArmPoseHook.afterGetArmPose(player, itemStackMain, itemStackOff, hand);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenGetArmPose(PlayerRendererBase overwriter)
    {
        if (this.overrideGetArmPoseHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetArmPoseHooks.length; i++) {
            if (this.overrideGetArmPoseHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetArmPoseHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetArmPoseHookTypes = new LinkedList<>();
    private final static List<String> overrideGetArmPoseHookTypes = new LinkedList<>();
    private final static List<String> afterGetArmPoseHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeGetArmPoseHooks;
    private PlayerRendererBase[] overrideGetArmPoseHooks;
    private PlayerRendererBase[] afterGetArmPoseHooks;
    public boolean isGetArmPoseModded;
    private static final Map<String, String[]> allBaseBeforeGetArmPoseSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetArmPoseInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetArmPoseSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetArmPoseInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetArmPoseSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetArmPoseInferiors = new Hashtable<>(0);

    // ############################################################################

    public static float getDeathMaxRotation(IPlayerRenderer target, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetDeathMaxRotationModded) {
           return playerRendererAPI.getDeathMaxRotation(player);
        } else {
           return target.superGetDeathMaxRotation(player);
        }
    }

    private float getDeathMaxRotation(AbstractClientPlayerEntity player)
    {
        if (this.beforeGetDeathMaxRotationHooks != null) {
            for (int i = this.beforeGetDeathMaxRotationHooks.length - 1; i >= 0; i--) {
                this.beforeGetDeathMaxRotationHooks[i].beforeGetDeathMaxRotation(player);
            }
        }

        float result;
        if (this.overrideGetDeathMaxRotationHooks != null) {
            result = this.overrideGetDeathMaxRotationHooks[this.overrideGetDeathMaxRotationHooks.length - 1].getDeathMaxRotation(player);
        } else {
            result = this.iPlayerRenderer.superGetDeathMaxRotation(player);
        }

        if (this.afterGetDeathMaxRotationHooks != null) {
            for (PlayerRendererBase afterGetDeathMaxRotationHook : this.afterGetDeathMaxRotationHooks) {
                afterGetDeathMaxRotationHook.afterGetDeathMaxRotation(player);
            }
        }

        return result;
    }

    protected PlayerRendererBase getOverwrittenGetDeathMaxRotation(PlayerRendererBase overwriter)
    {
        if (this.overrideGetDeathMaxRotationHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetDeathMaxRotationHooks.length; i++) {
            if (this.overrideGetDeathMaxRotationHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetDeathMaxRotationHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetDeathMaxRotationHookTypes = new LinkedList<>();
    private final static List<String> overrideGetDeathMaxRotationHookTypes = new LinkedList<>();
    private final static List<String> afterGetDeathMaxRotationHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeGetDeathMaxRotationHooks;
    private PlayerRendererBase[] overrideGetDeathMaxRotationHooks;
    private PlayerRendererBase[] afterGetDeathMaxRotationHooks;
    public boolean isGetDeathMaxRotationModded;
    private static final Map<String, String[]> allBaseBeforeGetDeathMaxRotationSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetDeathMaxRotationInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetDeathMaxRotationSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetDeathMaxRotationInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetDeathMaxRotationSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetDeathMaxRotationInferiors = new Hashtable<>(0);

    // ############################################################################

    public static PlayerModel<AbstractClientPlayerEntity> getEntityModel(IPlayerRenderer target)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetEntityModelModded) {
           return playerRendererAPI.getEntityModel();
        } else {
           return target.superGetEntityModel();
        }
    }

    private PlayerModel<AbstractClientPlayerEntity> getEntityModel()
    {
        if (this.beforeGetEntityModelHooks != null) {
            for (int i = this.beforeGetEntityModelHooks.length - 1; i >= 0; i--) {
                this.beforeGetEntityModelHooks[i].beforeGetEntityModel();
            }
        }

        PlayerModel<AbstractClientPlayerEntity> result;
        if (this.overrideGetEntityModelHooks != null) {
            result = this.overrideGetEntityModelHooks[this.overrideGetEntityModelHooks.length - 1].getEntityModel();
        } else {
            result = this.iPlayerRenderer.superGetEntityModel();
        }

        if (this.afterGetEntityModelHooks != null) {
            for (PlayerRendererBase afterGetEntityModelHook : this.afterGetEntityModelHooks) {
                afterGetEntityModelHook.afterGetEntityModel();
            }
        }

        return result;
    }

    protected PlayerRendererBase getOverwrittenGetEntityModel(PlayerRendererBase overwriter)
    {
        if (this.overrideGetEntityModelHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetEntityModelHooks.length; i++) {
            if (this.overrideGetEntityModelHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetEntityModelHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetEntityModelHookTypes = new LinkedList<>();
    private final static List<String> overrideGetEntityModelHookTypes = new LinkedList<>();
    private final static List<String> afterGetEntityModelHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeGetEntityModelHooks;
    private PlayerRendererBase[] overrideGetEntityModelHooks;
    private PlayerRendererBase[] afterGetEntityModelHooks;
    public boolean isGetEntityModelModded;
    private static final Map<String, String[]> allBaseBeforeGetEntityModelSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetEntityModelInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetEntityModelSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetEntityModelInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetEntityModelSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetEntityModelInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforeGetEntityTexture(CallbackInfoReturnable<ResourceLocation> callbackInfo, IPlayerRenderer target, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetEntityTextureModded) {
            playerRendererAPI.beforeGetEntityTexture(callbackInfo, player);
        }
    }

    private void beforeGetEntityTexture(CallbackInfoReturnable<ResourceLocation> callbackInfo, AbstractClientPlayerEntity player)
    {
        if (this.beforeGetEntityTextureHooks != null) {
            for (int i = this.beforeGetEntityTextureHooks.length - 1; i >= 0; i--) {
                this.beforeGetEntityTextureHooks[i].beforeGetEntityTexture(player);
            }
        }

        if (this.overrideGetEntityTextureHooks != null) {
            callbackInfo.setReturnValue(this.overrideGetEntityTextureHooks[this.overrideGetEntityTextureHooks.length - 1].getEntityTexture(player));
            callbackInfo.cancel();
        }
    }

    public static void afterGetEntityTexture(IPlayerRenderer target, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetEntityTextureModded) {
            playerRendererAPI.afterGetEntityTexture(player);
        }
    }

    private void afterGetEntityTexture(AbstractClientPlayerEntity player)
    {
        if (this.afterGetEntityTextureHooks != null) {
            for (PlayerRendererBase afterGetEntityTextureHook : this.afterGetEntityTextureHooks) {
                afterGetEntityTextureHook.afterGetEntityTexture(player);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenGetEntityTexture(PlayerRendererBase overwriter)
    {
        if (this.overrideGetEntityTextureHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetEntityTextureHooks.length; i++) {
            if (this.overrideGetEntityTextureHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetEntityTextureHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetEntityTextureHookTypes = new LinkedList<>();
    private final static List<String> overrideGetEntityTextureHookTypes = new LinkedList<>();
    private final static List<String> afterGetEntityTextureHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeGetEntityTextureHooks;
    private PlayerRendererBase[] overrideGetEntityTextureHooks;
    private PlayerRendererBase[] afterGetEntityTextureHooks;
    public boolean isGetEntityTextureModded;
    private static final Map<String, String[]> allBaseBeforeGetEntityTextureSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetEntityTextureInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetEntityTextureSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetEntityTextureInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetEntityTextureSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetEntityTextureInferiors = new Hashtable<>(0);

    // ############################################################################

    public static FontRenderer getFontRendererFromRenderManager(IPlayerRenderer target)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetFontRendererFromRenderManagerModded) {
           return playerRendererAPI.getFontRendererFromRenderManager();
        } else {
           return target.superGetFontRendererFromRenderManager();
        }
    }

    private FontRenderer getFontRendererFromRenderManager()
    {
        if (this.beforeGetFontRendererFromRenderManagerHooks != null) {
            for (int i = this.beforeGetFontRendererFromRenderManagerHooks.length - 1; i >= 0; i--) {
                this.beforeGetFontRendererFromRenderManagerHooks[i].beforeGetFontRendererFromRenderManager();
            }
        }

        FontRenderer result;
        if (this.overrideGetFontRendererFromRenderManagerHooks != null) {
            result = this.overrideGetFontRendererFromRenderManagerHooks[this.overrideGetFontRendererFromRenderManagerHooks.length - 1].getFontRendererFromRenderManager();
        } else {
            result = this.iPlayerRenderer.superGetFontRendererFromRenderManager();
        }

        if (this.afterGetFontRendererFromRenderManagerHooks != null) {
            for (PlayerRendererBase afterGetFontRendererFromRenderManagerHook : this.afterGetFontRendererFromRenderManagerHooks) {
                afterGetFontRendererFromRenderManagerHook.afterGetFontRendererFromRenderManager();
            }
        }

        return result;
    }

    protected PlayerRendererBase getOverwrittenGetFontRendererFromRenderManager(PlayerRendererBase overwriter)
    {
        if (this.overrideGetFontRendererFromRenderManagerHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetFontRendererFromRenderManagerHooks.length; i++) {
            if (this.overrideGetFontRendererFromRenderManagerHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetFontRendererFromRenderManagerHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetFontRendererFromRenderManagerHookTypes = new LinkedList<>();
    private final static List<String> overrideGetFontRendererFromRenderManagerHookTypes = new LinkedList<>();
    private final static List<String> afterGetFontRendererFromRenderManagerHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeGetFontRendererFromRenderManagerHooks;
    private PlayerRendererBase[] overrideGetFontRendererFromRenderManagerHooks;
    private PlayerRendererBase[] afterGetFontRendererFromRenderManagerHooks;
    public boolean isGetFontRendererFromRenderManagerModded;
    private static final Map<String, String[]> allBaseBeforeGetFontRendererFromRenderManagerSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetFontRendererFromRenderManagerInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetFontRendererFromRenderManagerSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetFontRendererFromRenderManagerInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetFontRendererFromRenderManagerSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetFontRendererFromRenderManagerInferiors = new Hashtable<>(0);

    // ############################################################################

    public static EntityRendererManager getRenderManager(IPlayerRenderer target)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetRenderManagerModded) {
           return playerRendererAPI.getRenderManager();
        } else {
           return target.superGetRenderManager();
        }
    }

    private EntityRendererManager getRenderManager()
    {
        if (this.beforeGetRenderManagerHooks != null) {
            for (int i = this.beforeGetRenderManagerHooks.length - 1; i >= 0; i--) {
                this.beforeGetRenderManagerHooks[i].beforeGetRenderManager();
            }
        }

        EntityRendererManager result;
        if (this.overrideGetRenderManagerHooks != null) {
            result = this.overrideGetRenderManagerHooks[this.overrideGetRenderManagerHooks.length - 1].getRenderManager();
        } else {
            result = this.iPlayerRenderer.superGetRenderManager();
        }

        if (this.afterGetRenderManagerHooks != null) {
            for (PlayerRendererBase afterGetRenderManagerHook : this.afterGetRenderManagerHooks) {
                afterGetRenderManagerHook.afterGetRenderManager();
            }
        }

        return result;
    }

    protected PlayerRendererBase getOverwrittenGetRenderManager(PlayerRendererBase overwriter)
    {
        if (this.overrideGetRenderManagerHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetRenderManagerHooks.length; i++) {
            if (this.overrideGetRenderManagerHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetRenderManagerHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetRenderManagerHookTypes = new LinkedList<>();
    private final static List<String> overrideGetRenderManagerHookTypes = new LinkedList<>();
    private final static List<String> afterGetRenderManagerHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeGetRenderManagerHooks;
    private PlayerRendererBase[] overrideGetRenderManagerHooks;
    private PlayerRendererBase[] afterGetRenderManagerHooks;
    public boolean isGetRenderManagerModded;
    private static final Map<String, String[]> allBaseBeforeGetRenderManagerSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetRenderManagerInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetRenderManagerSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetRenderManagerInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetRenderManagerSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetRenderManagerInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforeGetRenderOffset(CallbackInfoReturnable<Vec3d> callbackInfo, IPlayerRenderer target, AbstractClientPlayerEntity player, float partialTicks)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetRenderOffsetModded) {
            playerRendererAPI.beforeGetRenderOffset(callbackInfo, player, partialTicks);
        }
    }

    private void beforeGetRenderOffset(CallbackInfoReturnable<Vec3d> callbackInfo, AbstractClientPlayerEntity player, float partialTicks)
    {
        if (this.beforeGetRenderOffsetHooks != null) {
            for (int i = this.beforeGetRenderOffsetHooks.length - 1; i >= 0; i--) {
                this.beforeGetRenderOffsetHooks[i].beforeGetRenderOffset(player, partialTicks);
            }
        }

        if (this.overrideGetRenderOffsetHooks != null) {
            callbackInfo.setReturnValue(this.overrideGetRenderOffsetHooks[this.overrideGetRenderOffsetHooks.length - 1].getRenderOffset(player, partialTicks));
            callbackInfo.cancel();
        }
    }

    public static void afterGetRenderOffset(IPlayerRenderer target, AbstractClientPlayerEntity player, float partialTicks)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetRenderOffsetModded) {
            playerRendererAPI.afterGetRenderOffset(player, partialTicks);
        }
    }

    private void afterGetRenderOffset(AbstractClientPlayerEntity player, float partialTicks)
    {
        if (this.afterGetRenderOffsetHooks != null) {
            for (PlayerRendererBase afterGetRenderOffsetHook : this.afterGetRenderOffsetHooks) {
                afterGetRenderOffsetHook.afterGetRenderOffset(player, partialTicks);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenGetRenderOffset(PlayerRendererBase overwriter)
    {
        if (this.overrideGetRenderOffsetHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetRenderOffsetHooks.length; i++) {
            if (this.overrideGetRenderOffsetHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetRenderOffsetHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetRenderOffsetHookTypes = new LinkedList<>();
    private final static List<String> overrideGetRenderOffsetHookTypes = new LinkedList<>();
    private final static List<String> afterGetRenderOffsetHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeGetRenderOffsetHooks;
    private PlayerRendererBase[] overrideGetRenderOffsetHooks;
    private PlayerRendererBase[] afterGetRenderOffsetHooks;
    public boolean isGetRenderOffsetModded;
    private static final Map<String, String[]> allBaseBeforeGetRenderOffsetSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetRenderOffsetInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetRenderOffsetSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetRenderOffsetInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetRenderOffsetSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetRenderOffsetInferiors = new Hashtable<>(0);

    // ############################################################################

    public static float getSwingProgress(IPlayerRenderer target, AbstractClientPlayerEntity player, float partialTickTime)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isGetSwingProgressModded) {
           return playerRendererAPI.getSwingProgress(player, partialTickTime);
        } else {
           return target.superGetSwingProgress(player, partialTickTime);
        }
    }

    private float getSwingProgress(AbstractClientPlayerEntity player, float partialTickTime)
    {
        if (this.beforeGetSwingProgressHooks != null) {
            for (int i = this.beforeGetSwingProgressHooks.length - 1; i >= 0; i--) {
                this.beforeGetSwingProgressHooks[i].beforeGetSwingProgress(player, partialTickTime);
            }
        }

        float result;
        if (this.overrideGetSwingProgressHooks != null) {
            result = this.overrideGetSwingProgressHooks[this.overrideGetSwingProgressHooks.length - 1].getSwingProgress(player, partialTickTime);
        } else {
            result = this.iPlayerRenderer.superGetSwingProgress(player, partialTickTime);
        }

        if (this.afterGetSwingProgressHooks != null) {
            for (PlayerRendererBase afterGetSwingProgressHook : this.afterGetSwingProgressHooks) {
                afterGetSwingProgressHook.afterGetSwingProgress(player, partialTickTime);
            }
        }

        return result;
    }

    protected PlayerRendererBase getOverwrittenGetSwingProgress(PlayerRendererBase overwriter)
    {
        if (this.overrideGetSwingProgressHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideGetSwingProgressHooks.length; i++) {
            if (this.overrideGetSwingProgressHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideGetSwingProgressHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeGetSwingProgressHookTypes = new LinkedList<>();
    private final static List<String> overrideGetSwingProgressHookTypes = new LinkedList<>();
    private final static List<String> afterGetSwingProgressHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeGetSwingProgressHooks;
    private PlayerRendererBase[] overrideGetSwingProgressHooks;
    private PlayerRendererBase[] afterGetSwingProgressHooks;
    public boolean isGetSwingProgressModded;
    private static final Map<String, String[]> allBaseBeforeGetSwingProgressSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeGetSwingProgressInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetSwingProgressSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideGetSwingProgressInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetSwingProgressSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterGetSwingProgressInferiors = new Hashtable<>(0);

    // ############################################################################

    public static float handleRotationFloat(IPlayerRenderer target, AbstractClientPlayerEntity player, float partialTicks)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isHandleRotationFloatModded) {
           return playerRendererAPI.handleRotationFloat(player, partialTicks);
        } else {
           return target.superHandleRotationFloat(player, partialTicks);
        }
    }

    private float handleRotationFloat(AbstractClientPlayerEntity player, float partialTicks)
    {
        if (this.beforeHandleRotationFloatHooks != null) {
            for (int i = this.beforeHandleRotationFloatHooks.length - 1; i >= 0; i--) {
                this.beforeHandleRotationFloatHooks[i].beforeHandleRotationFloat(player, partialTicks);
            }
        }

        float result;
        if (this.overrideHandleRotationFloatHooks != null) {
            result = this.overrideHandleRotationFloatHooks[this.overrideHandleRotationFloatHooks.length - 1].handleRotationFloat(player, partialTicks);
        } else {
            result = this.iPlayerRenderer.superHandleRotationFloat(player, partialTicks);
        }

        if (this.afterHandleRotationFloatHooks != null) {
            for (PlayerRendererBase afterHandleRotationFloatHook : this.afterHandleRotationFloatHooks) {
                afterHandleRotationFloatHook.afterHandleRotationFloat(player, partialTicks);
            }
        }

        return result;
    }

    protected PlayerRendererBase getOverwrittenHandleRotationFloat(PlayerRendererBase overwriter)
    {
        if (this.overrideHandleRotationFloatHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideHandleRotationFloatHooks.length; i++) {
            if (this.overrideHandleRotationFloatHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideHandleRotationFloatHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeHandleRotationFloatHookTypes = new LinkedList<>();
    private final static List<String> overrideHandleRotationFloatHookTypes = new LinkedList<>();
    private final static List<String> afterHandleRotationFloatHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeHandleRotationFloatHooks;
    private PlayerRendererBase[] overrideHandleRotationFloatHooks;
    private PlayerRendererBase[] afterHandleRotationFloatHooks;
    public boolean isHandleRotationFloatModded;
    private static final Map<String, String[]> allBaseBeforeHandleRotationFloatSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeHandleRotationFloatInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideHandleRotationFloatSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideHandleRotationFloatInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterHandleRotationFloatSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterHandleRotationFloatInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforePreRenderCallback(CallbackInfo callbackInfo, IPlayerRenderer target, AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isPreRenderCallbackModded) {
            playerRendererAPI.beforePreRenderCallback(callbackInfo, player, matrixStack, partialTickTime);
        }
    }

    private void beforePreRenderCallback(CallbackInfo callbackInfo, AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime)
    {
        if (this.beforePreRenderCallbackHooks != null) {
            for (int i = this.beforePreRenderCallbackHooks.length - 1; i >= 0; i--) {
                this.beforePreRenderCallbackHooks[i].beforePreRenderCallback(player, matrixStack, partialTickTime);
            }
        }

        if (this.overridePreRenderCallbackHooks != null) {
            this.overridePreRenderCallbackHooks[this.overridePreRenderCallbackHooks.length - 1].preRenderCallback(player, matrixStack, partialTickTime);
            callbackInfo.cancel();
        }
    }

    public static void afterPreRenderCallback(IPlayerRenderer target, AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isPreRenderCallbackModded) {
            playerRendererAPI.afterPreRenderCallback(player, matrixStack, partialTickTime);
        }
    }

    private void afterPreRenderCallback(AbstractClientPlayerEntity player, MatrixStack matrixStack, float partialTickTime)
    {
        if (this.afterPreRenderCallbackHooks != null) {
            for (PlayerRendererBase afterPreRenderCallbackHook : this.afterPreRenderCallbackHooks) {
                afterPreRenderCallbackHook.afterPreRenderCallback(player, matrixStack, partialTickTime);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenPreRenderCallback(PlayerRendererBase overwriter)
    {
        if (this.overridePreRenderCallbackHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overridePreRenderCallbackHooks.length; i++) {
            if (this.overridePreRenderCallbackHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overridePreRenderCallbackHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforePreRenderCallbackHookTypes = new LinkedList<>();
    private final static List<String> overridePreRenderCallbackHookTypes = new LinkedList<>();
    private final static List<String> afterPreRenderCallbackHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforePreRenderCallbackHooks;
    private PlayerRendererBase[] overridePreRenderCallbackHooks;
    private PlayerRendererBase[] afterPreRenderCallbackHooks;
    public boolean isPreRenderCallbackModded;
    private static final Map<String, String[]> allBaseBeforePreRenderCallbackSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforePreRenderCallbackInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverridePreRenderCallbackSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverridePreRenderCallbackInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterPreRenderCallbackSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterPreRenderCallbackInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforeRender(CallbackInfo callbackInfo, IPlayerRenderer target, AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderModded) {
            playerRendererAPI.beforeRender(callbackInfo, player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }
    }

    private void beforeRender(CallbackInfo callbackInfo, AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        if (this.beforeRenderHooks != null) {
            for (int i = this.beforeRenderHooks.length - 1; i >= 0; i--) {
                this.beforeRenderHooks[i].beforeRender(player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
            }
        }

        if (this.overrideRenderHooks != null) {
            this.overrideRenderHooks[this.overrideRenderHooks.length - 1].render(player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
            callbackInfo.cancel();
        }
    }

    public static void afterRender(IPlayerRenderer target, AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderModded) {
            playerRendererAPI.afterRender(player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }
    }

    private void afterRender(AbstractClientPlayerEntity player, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        if (this.afterRenderHooks != null) {
            for (PlayerRendererBase afterRenderHook : this.afterRenderHooks) {
                afterRenderHook.afterRender(player, entityYaw, partialTicks, matrixStack, buffer, packedLight);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenRender(PlayerRendererBase overwriter)
    {
        if (this.overrideRenderHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideRenderHooks.length; i++) {
            if (this.overrideRenderHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideRenderHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeRenderHookTypes = new LinkedList<>();
    private final static List<String> overrideRenderHookTypes = new LinkedList<>();
    private final static List<String> afterRenderHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeRenderHooks;
    private PlayerRendererBase[] overrideRenderHooks;
    private PlayerRendererBase[] afterRenderHooks;
    public boolean isRenderModded;
    private static final Map<String, String[]> allBaseBeforeRenderSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeRenderInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforeRenderItem(CallbackInfo callbackInfo, IPlayerRenderer target, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderItemModded) {
            playerRendererAPI.beforeRenderItem(callbackInfo, matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
        }
    }

    private void beforeRenderItem(CallbackInfo callbackInfo, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear)
    {
        if (this.beforeRenderItemHooks != null) {
            for (int i = this.beforeRenderItemHooks.length - 1; i >= 0; i--) {
                this.beforeRenderItemHooks[i].beforeRenderItem(matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
            }
        }

        if (this.overrideRenderItemHooks != null) {
            this.overrideRenderItemHooks[this.overrideRenderItemHooks.length - 1].renderItem(matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
            callbackInfo.cancel();
        }
    }

    public static void afterRenderItem(IPlayerRenderer target, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderItemModded) {
            playerRendererAPI.afterRenderItem(matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
        }
    }

    private void afterRenderItem(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player, ModelRenderer rendererArm, ModelRenderer rendererArmwear)
    {
        if (this.afterRenderItemHooks != null) {
            for (PlayerRendererBase afterRenderItemHook : this.afterRenderItemHooks) {
                afterRenderItemHook.afterRenderItem(matrixStack, buffer, combinedLight, player, rendererArm, rendererArmwear);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenRenderItem(PlayerRendererBase overwriter)
    {
        if (this.overrideRenderItemHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideRenderItemHooks.length; i++) {
            if (this.overrideRenderItemHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideRenderItemHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeRenderItemHookTypes = new LinkedList<>();
    private final static List<String> overrideRenderItemHookTypes = new LinkedList<>();
    private final static List<String> afterRenderItemHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeRenderItemHooks;
    private PlayerRendererBase[] overrideRenderItemHooks;
    private PlayerRendererBase[] afterRenderItemHooks;
    public boolean isRenderItemModded;
    private static final Map<String, String[]> allBaseBeforeRenderItemSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeRenderItemInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderItemSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderItemInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderItemSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderItemInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforeRenderLeftArm(CallbackInfo callbackInfo, IPlayerRenderer target, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderLeftArmModded) {
            playerRendererAPI.beforeRenderLeftArm(callbackInfo, matrixStack, buffer, combinedLight, player);
        }
    }

    private void beforeRenderLeftArm(CallbackInfo callbackInfo, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        if (this.beforeRenderLeftArmHooks != null) {
            for (int i = this.beforeRenderLeftArmHooks.length - 1; i >= 0; i--) {
                this.beforeRenderLeftArmHooks[i].beforeRenderLeftArm(matrixStack, buffer, combinedLight, player);
            }
        }

        if (this.overrideRenderLeftArmHooks != null) {
            this.overrideRenderLeftArmHooks[this.overrideRenderLeftArmHooks.length - 1].renderLeftArm(matrixStack, buffer, combinedLight, player);
            callbackInfo.cancel();
        }
    }

    public static void afterRenderLeftArm(IPlayerRenderer target, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderLeftArmModded) {
            playerRendererAPI.afterRenderLeftArm(matrixStack, buffer, combinedLight, player);
        }
    }

    private void afterRenderLeftArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        if (this.afterRenderLeftArmHooks != null) {
            for (PlayerRendererBase afterRenderLeftArmHook : this.afterRenderLeftArmHooks) {
                afterRenderLeftArmHook.afterRenderLeftArm(matrixStack, buffer, combinedLight, player);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenRenderLeftArm(PlayerRendererBase overwriter)
    {
        if (this.overrideRenderLeftArmHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideRenderLeftArmHooks.length; i++) {
            if (this.overrideRenderLeftArmHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideRenderLeftArmHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeRenderLeftArmHookTypes = new LinkedList<>();
    private final static List<String> overrideRenderLeftArmHookTypes = new LinkedList<>();
    private final static List<String> afterRenderLeftArmHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeRenderLeftArmHooks;
    private PlayerRendererBase[] overrideRenderLeftArmHooks;
    private PlayerRendererBase[] afterRenderLeftArmHooks;
    public boolean isRenderLeftArmModded;
    private static final Map<String, String[]> allBaseBeforeRenderLeftArmSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeRenderLeftArmInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderLeftArmSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderLeftArmInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderLeftArmSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderLeftArmInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforeRenderName(CallbackInfo callbackInfo, IPlayerRenderer target, AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderNameModded) {
            playerRendererAPI.beforeRenderName(callbackInfo, player, displayName, matrixStack, buffer, packedLight);
        }
    }

    private void beforeRenderName(CallbackInfo callbackInfo, AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        if (this.beforeRenderNameHooks != null) {
            for (int i = this.beforeRenderNameHooks.length - 1; i >= 0; i--) {
                this.beforeRenderNameHooks[i].beforeRenderName(player, displayName, matrixStack, buffer, packedLight);
            }
        }

        if (this.overrideRenderNameHooks != null) {
            this.overrideRenderNameHooks[this.overrideRenderNameHooks.length - 1].renderName(player, displayName, matrixStack, buffer, packedLight);
            callbackInfo.cancel();
        }
    }

    public static void afterRenderName(IPlayerRenderer target, AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderNameModded) {
            playerRendererAPI.afterRenderName(player, displayName, matrixStack, buffer, packedLight);
        }
    }

    private void afterRenderName(AbstractClientPlayerEntity player, String displayName, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight)
    {
        if (this.afterRenderNameHooks != null) {
            for (PlayerRendererBase afterRenderNameHook : this.afterRenderNameHooks) {
                afterRenderNameHook.afterRenderName(player, displayName, matrixStack, buffer, packedLight);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenRenderName(PlayerRendererBase overwriter)
    {
        if (this.overrideRenderNameHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideRenderNameHooks.length; i++) {
            if (this.overrideRenderNameHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideRenderNameHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeRenderNameHookTypes = new LinkedList<>();
    private final static List<String> overrideRenderNameHookTypes = new LinkedList<>();
    private final static List<String> afterRenderNameHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeRenderNameHooks;
    private PlayerRendererBase[] overrideRenderNameHooks;
    private PlayerRendererBase[] afterRenderNameHooks;
    public boolean isRenderNameModded;
    private static final Map<String, String[]> allBaseBeforeRenderNameSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeRenderNameInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderNameSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderNameInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderNameSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderNameInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforeRenderRightArm(CallbackInfo callbackInfo, IPlayerRenderer target, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderRightArmModded) {
            playerRendererAPI.beforeRenderRightArm(callbackInfo, matrixStack, buffer, combinedLight, player);
        }
    }

    private void beforeRenderRightArm(CallbackInfo callbackInfo, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        if (this.beforeRenderRightArmHooks != null) {
            for (int i = this.beforeRenderRightArmHooks.length - 1; i >= 0; i--) {
                this.beforeRenderRightArmHooks[i].beforeRenderRightArm(matrixStack, buffer, combinedLight, player);
            }
        }

        if (this.overrideRenderRightArmHooks != null) {
            this.overrideRenderRightArmHooks[this.overrideRenderRightArmHooks.length - 1].renderRightArm(matrixStack, buffer, combinedLight, player);
            callbackInfo.cancel();
        }
    }

    public static void afterRenderRightArm(IPlayerRenderer target, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isRenderRightArmModded) {
            playerRendererAPI.afterRenderRightArm(matrixStack, buffer, combinedLight, player);
        }
    }

    private void afterRenderRightArm(MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, AbstractClientPlayerEntity player)
    {
        if (this.afterRenderRightArmHooks != null) {
            for (PlayerRendererBase afterRenderRightArmHook : this.afterRenderRightArmHooks) {
                afterRenderRightArmHook.afterRenderRightArm(matrixStack, buffer, combinedLight, player);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenRenderRightArm(PlayerRendererBase overwriter)
    {
        if (this.overrideRenderRightArmHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideRenderRightArmHooks.length; i++) {
            if (this.overrideRenderRightArmHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideRenderRightArmHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeRenderRightArmHookTypes = new LinkedList<>();
    private final static List<String> overrideRenderRightArmHookTypes = new LinkedList<>();
    private final static List<String> afterRenderRightArmHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeRenderRightArmHooks;
    private PlayerRendererBase[] overrideRenderRightArmHooks;
    private PlayerRendererBase[] afterRenderRightArmHooks;
    public boolean isRenderRightArmModded;
    private static final Map<String, String[]> allBaseBeforeRenderRightArmSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeRenderRightArmInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderRightArmSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideRenderRightArmInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderRightArmSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterRenderRightArmInferiors = new Hashtable<>(0);

    // ############################################################################

    public static void beforeSetModelVisibilities(CallbackInfo callbackInfo, IPlayerRenderer target, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isSetModelVisibilitiesModded) {
            playerRendererAPI.beforeSetModelVisibilities(callbackInfo, player);
        }
    }

    private void beforeSetModelVisibilities(CallbackInfo callbackInfo, AbstractClientPlayerEntity player)
    {
        if (this.beforeSetModelVisibilitiesHooks != null) {
            for (int i = this.beforeSetModelVisibilitiesHooks.length - 1; i >= 0; i--) {
                this.beforeSetModelVisibilitiesHooks[i].beforeSetModelVisibilities(player);
            }
        }

        if (this.overrideSetModelVisibilitiesHooks != null) {
            this.overrideSetModelVisibilitiesHooks[this.overrideSetModelVisibilitiesHooks.length - 1].setModelVisibilities(player);
            callbackInfo.cancel();
        }
    }

    public static void afterSetModelVisibilities(IPlayerRenderer target, AbstractClientPlayerEntity player)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isSetModelVisibilitiesModded) {
            playerRendererAPI.afterSetModelVisibilities(player);
        }
    }

    private void afterSetModelVisibilities(AbstractClientPlayerEntity player)
    {
        if (this.afterSetModelVisibilitiesHooks != null) {
            for (PlayerRendererBase afterSetModelVisibilitiesHook : this.afterSetModelVisibilitiesHooks) {
                afterSetModelVisibilitiesHook.afterSetModelVisibilities(player);
            }
        }
    }

    protected PlayerRendererBase getOverwrittenSetModelVisibilities(PlayerRendererBase overwriter)
    {
        if (this.overrideSetModelVisibilitiesHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideSetModelVisibilitiesHooks.length; i++) {
            if (this.overrideSetModelVisibilitiesHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideSetModelVisibilitiesHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeSetModelVisibilitiesHookTypes = new LinkedList<>();
    private final static List<String> overrideSetModelVisibilitiesHookTypes = new LinkedList<>();
    private final static List<String> afterSetModelVisibilitiesHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeSetModelVisibilitiesHooks;
    private PlayerRendererBase[] overrideSetModelVisibilitiesHooks;
    private PlayerRendererBase[] afterSetModelVisibilitiesHooks;
    public boolean isSetModelVisibilitiesModded;
    private static final Map<String, String[]> allBaseBeforeSetModelVisibilitiesSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeSetModelVisibilitiesInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetModelVisibilitiesSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideSetModelVisibilitiesInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetModelVisibilitiesSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterSetModelVisibilitiesInferiors = new Hashtable<>(0);

    // ############################################################################

    public static boolean shouldRender(IPlayerRenderer target, AbstractClientPlayerEntity player, ClippingHelperImpl camera, double camX, double camY, double camZ)
    {
        PlayerRendererAPI playerRendererAPI = target.getPlayerRendererAPI();
        if (playerRendererAPI != null && playerRendererAPI.isShouldRenderModded) {
           return playerRendererAPI.shouldRender(player, camera, camX, camY, camZ);
        } else {
           return target.superShouldRender(player, camera, camX, camY, camZ);
        }
    }

    private boolean shouldRender(AbstractClientPlayerEntity player, ClippingHelperImpl camera, double camX, double camY, double camZ)
    {
        if (this.beforeShouldRenderHooks != null) {
            for (int i = this.beforeShouldRenderHooks.length - 1; i >= 0; i--) {
                this.beforeShouldRenderHooks[i].beforeShouldRender(player, camera, camX, camY, camZ);
            }
        }

        boolean result;
        if (this.overrideShouldRenderHooks != null) {
            result = this.overrideShouldRenderHooks[this.overrideShouldRenderHooks.length - 1].shouldRender(player, camera, camX, camY, camZ);
        } else {
            result = this.iPlayerRenderer.superShouldRender(player, camera, camX, camY, camZ);
        }

        if (this.afterShouldRenderHooks != null) {
            for (PlayerRendererBase afterShouldRenderHook : this.afterShouldRenderHooks) {
                afterShouldRenderHook.afterShouldRender(player, camera, camX, camY, camZ);
            }
        }

        return result;
    }

    protected PlayerRendererBase getOverwrittenShouldRender(PlayerRendererBase overwriter)
    {
        if (this.overrideShouldRenderHooks == null) {
            return overwriter;
        }

        for (int i = 0; i < this.overrideShouldRenderHooks.length; i++) {
            if (this.overrideShouldRenderHooks[i] == overwriter) {
                if (i == 0) {
                    return null;
                } else {
                    return this.overrideShouldRenderHooks[i - 1];
                }
            }
        }

        return overwriter;
    }

    private final static List<String> beforeShouldRenderHookTypes = new LinkedList<>();
    private final static List<String> overrideShouldRenderHookTypes = new LinkedList<>();
    private final static List<String> afterShouldRenderHookTypes = new LinkedList<>();
    private PlayerRendererBase[] beforeShouldRenderHooks;
    private PlayerRendererBase[] overrideShouldRenderHooks;
    private PlayerRendererBase[] afterShouldRenderHooks;
    public boolean isShouldRenderModded;
    private static final Map<String, String[]> allBaseBeforeShouldRenderSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseBeforeShouldRenderInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideShouldRenderSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseOverrideShouldRenderInferiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterShouldRenderSuperiors = new Hashtable<>(0);
    private static final Map<String, String[]> allBaseAfterShouldRenderInferiors = new Hashtable<>(0);
}