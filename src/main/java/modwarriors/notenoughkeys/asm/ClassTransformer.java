package modwarriors.notenoughkeys.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author TheTemportalist
 */
public class ClassTransformer implements IClassTransformer {

	/**
	 * @param name            The full path name of the original class (can be obfuscated)
	 * @param transformedName The full path name of the new class (non obfuscated)
	 * @param basicClass      The bytes of the original .class
	 * @return The bytes of the new .class
	 */
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.client.settings.KeyBinding")) {
			// check for obf
			boolean isObfedEnvironment = !name.equals(transformedName);
			// Move to manipulable object
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);

			for (FieldNode field : classNode.fields) {
				if (field.name.equals("hash") && field.desc
						.equals("Lnet/minecraft/util/IntHashMap;")) {
					field.desc = "Ljava/util/List;";
					field.name = "hash";
				}
			}
			InsnList nodesToInject = new InsnList();
			for (MethodNode method : classNode.methods) {
				if (method.name.equals("onTick") && method.desc.equals("(I)V")) {
					AbstractInsnNode instruction = method.instructions.getFirst();
					while (instruction.getOpcode() != GETSTATIC) {
						instruction.getNext();
					}

					// -> ILOAD
					instruction.getNext();
					// Remove GETSTATIC
					method.instructions.remove(instruction.getPrevious());
					// -> INVOKEVIRTUAL
					instruction.getNext();
					// Change INVOKEVIRTUAL
					((MethodInsnNode) instruction).setOpcode(INVOKESTATIC);
					((MethodInsnNode) instruction).owner = "net/minecraft/client/settings/KeyBinding";
					((MethodInsnNode) instruction).name = "getKeyBindingsWithKey";
					((MethodInsnNode) instruction).desc = "(I)[Lnet/minecraft/client/settings/KeyBinding;";
					// -> CHECKCAST
					instruction.getNext();
					// -> ASTORE
					instruction.getNext();
					// Remove CHECKCAST
					method.instructions.remove(instruction.getPrevious());
					// -> L3
					instruction.getNext();
					LabelNode L3 = (LabelNode) instruction;
					// -> ALOAD
					instruction.getNext();
					// -> IFNULL
					instruction.getNext();
					// -> L4
					instruction.getNext();
					LabelNode L4 = (LabelNode) instruction;
					// Remove IFNULL
					method.instructions.remove(instruction.getPrevious());
					// Inject List of nodes
					// ARRAYLENGTH
					nodesToInject.add(new InsnNode(ARRAYLENGTH));
					// ISTORE
					nodesToInject.add(new VarInsnNode(ISTORE, 2));
					method.instructions.insertBefore(instruction, nodesToInject);
					nodesToInject.clear();
					// Remove next 7
					for (int i = 0; i < 6; i++) {
						method.instructions.remove(instruction.getNext());
					}
					// -> L1
					instruction.getNext();
					LabelNode L1 = (LabelNode) instruction;
					// Inject many things
					nodesToInject.add(new InsnNode(ICONST_0));
					nodesToInject.add(new VarInsnNode(ISTORE, 3));
					LabelNode L5 = new LabelNode();
					nodesToInject.add(L5);
					nodesToInject.add(new VarInsnNode(ILOAD, 3));
					nodesToInject.add(new VarInsnNode(ILOAD, 2));
					nodesToInject.add(new JumpInsnNode(IF_ICMPGE, (LabelNode) instruction));
					nodesToInject.add(new VarInsnNode(ALOAD, 1));
					nodesToInject.add(new VarInsnNode(ILOAD, 3));
					nodesToInject.add(new InsnNode(AALOAD));
					nodesToInject.add(new VarInsnNode(ASTORE, 4));
					LabelNode L6 = new LabelNode();
					nodesToInject.add(L6);
					nodesToInject.add(new VarInsnNode(ALOAD, 4));
					LabelNode L7 = new LabelNode();
					nodesToInject.add(new JumpInsnNode(IFNULL, L7));
					nodesToInject.add(new VarInsnNode(ALOAD, 4));
					nodesToInject.add(new InsnNode(DUP));
					nodesToInject.add(new FieldInsnNode(
							GETFIELD, "net/minecraft/client/settings/KeyBinding",
							"pressTime", "I"
					));
					nodesToInject.add(new InsnNode(ICONST_1));
					nodesToInject.add(new InsnNode(IADD));
					nodesToInject.add(new FieldInsnNode(
							PUTFIELD, "net/minecraft/client/settings/KeyBinding", "pressTime", "I"
					));
					nodesToInject.add(L7);
					nodesToInject.add(new IincInsnNode(3, 1));
					nodesToInject.add(new JumpInsnNode(GOTO, L5));
					// Inserts before L1
					method.instructions.insertBefore(instruction, nodesToInject);
					nodesToInject.clear();
					// We are @ L1
					// -> L5
					instruction.getNext();

					LocalVariableNode localVar = method.localVariables.get(0);
					localVar.start = L6;
					localVar.end = L7;
					localVar.index = 4;
					method.localVariables.add(new LocalVariableNode("arr$",
							"[Lnet/minecraft/client/settings/KeyBinding;", null, L3, L1, 1));
					method.localVariables.add(new LocalVariableNode("len$", "I", null, L4, L1, 2));
					method.localVariables.add(new LocalVariableNode("i$", "I", null, L5, L1, 3));

					// DONE w/ func
				}
				else if (method.name.equals("setKeyBindState") && method.desc.equals("(IZ)V")) {
					AbstractInsnNode instruction = method.instructions.getFirst();
					while (instruction.getOpcode() != GETSTATIC) {
						instruction.getNext();
					}

					// L2
					// -> ILOAD
					instruction.getNext();
					// -> Remove GETSTATIC
					method.instructions.remove(instruction.getPrevious());
					// -> InvokeVirtual
					instruction.getNext();
					((FieldInsnNode) instruction).setOpcode(INVOKESTATIC);
					((FieldInsnNode) instruction).owner = "net/minecraft/client/settings/KeyBinding";
					((FieldInsnNode) instruction).name = "getKeyBindingsWithKey";
					((FieldInsnNode) instruction).desc = "(I)[Lnet/minecraft/client/settings/KeyBinding;";
					// -> CheckCast
					instruction.getNext();
					// -> ASTORE
					instruction.getNext();
					// Remove CheckCast
					method.instructions.remove(instruction.getPrevious());
					// -> L3
					instruction.getNext();
					LabelNode L3 = (LabelNode) instruction;
					// -> ALoad
					instruction.getNext();
					// -> IFNULL
					instruction.getNext();
					// -> L4
					instruction.getNext();
					LabelNode L4 = (LabelNode) instruction;
					// Remove IFNULL
					method.instructions.remove(instruction.getPrevious());
					// Add nodes
					nodesToInject.add(new InsnNode(ARRAYLENGTH));
					nodesToInject.add(new VarInsnNode(ISTORE, 3));
					method.instructions.insertBefore(instruction, nodesToInject);
					nodesToInject.clear();
					for (int i = 0; i < 3; i++) {
						method.instructions.remove(instruction.getNext());
					}
					// -> L1
					instruction.getNext();
					LabelNode L1 = (LabelNode) instruction;
					// Add Some Nodes
					nodesToInject.add(new InsnNode(ICONST_0));
					nodesToInject.add(new VarInsnNode(ISTORE, 4));
					// L5
					LabelNode L5 = new LabelNode();
					nodesToInject.add(L5);
					nodesToInject.add(new VarInsnNode(ILOAD, 4));
					nodesToInject.add(new VarInsnNode(ILOAD, 3));
					nodesToInject.add(new JumpInsnNode(IF_ICMPGE, L1));
					nodesToInject.add(new VarInsnNode(ALOAD, 2));
					nodesToInject.add(new VarInsnNode(ILOAD, 4));
					nodesToInject.add(new InsnNode(AALOAD));
					nodesToInject.add(new VarInsnNode(ASTORE, 5));
					// L6
					LabelNode L6 = new LabelNode();
					nodesToInject.add(L6);
					nodesToInject.add(new VarInsnNode(ALOAD, 5));
					LabelNode L7 = new LabelNode();
					nodesToInject.add(new JumpInsnNode(IFNULL, L7));
					// L8
					nodesToInject.add(new VarInsnNode(ALOAD, 5));
					nodesToInject.add(new VarInsnNode(ILOAD, 1));
					nodesToInject.add(new FieldInsnNode(PUTFIELD,
							"net/minecraft/client/settings/KeyBinding", "pressed", "Z"));
					// L7
					nodesToInject.add(L7);
					nodesToInject.add(new IincInsnNode(4, 1));
					nodesToInject.add(new JumpInsnNode(GOTO, L5));
					method.instructions.insertBefore(instruction, nodesToInject);
					nodesToInject.clear();
					// -> Return
					instruction.getNext();
					// -> L5 (local vars)
					instruction.getNext();

					LocalVariableNode localVar = method.localVariables.get(0);
					localVar.start = L6;
					localVar.end = L7;
					localVar.index = 5;
					method.localVariables.add(new LocalVariableNode("arr$",
							"[Lnet/minecraft/client/settings/KeyBinding;", null, L3, L1, 2));
					method.localVariables.add(new LocalVariableNode("len$", "I", null, L4, L1, 3));
					method.localVariables.add(new LocalVariableNode("i$", "I", null, L5, L1, 3));

				}
				else if (method.name.equals("<init>") && method.desc
						.equals("(Ljava/lang/String;ILjava/lang/String;)V")) {
					AbstractInsnNode instruction = method.instructions.getFirst();
					// -> L6 GetStatic
					for (int i = 0; i < 26; i++) {
						instruction.getNext();
					}
					((FieldInsnNode) instruction).owner = "net/minecraft/client/settings/KeyBinding";
					((FieldInsnNode) instruction).desc = "Ljava/util/ArrayList;";
					// Remove ILoad 2
					method.instructions.remove(instruction.getNext());
					// -> ALoad 0
					instruction.getNext();
					// -> InvokeVirtual
					instruction.getNext();
					// modify
					((FieldInsnNode) instruction).owner = "java/util/ArrayList";
					((FieldInsnNode) instruction).name = "add";
					((FieldInsnNode) instruction).desc = "(Ljava/lang/Object;)Z";
					// -> L7
					instruction.getNext();
					// Insert pop
					method.instructions.insertBefore(instruction, new InsnNode(POP));

				}

			}

			// getKeyBindingsWithKey method
			MethodNode method = new MethodNode(ACC_PRIVATE | ACC_STATIC,
					"getKeyBindingsWithKey", "(I)[Lmodwarriors/notenoughkeys/KeyBinding2;", null,
					new String[0]);
			// L0
			LabelNode L0 = new LabelNode();
			method.instructions.add(L0);
			// todo 'NEW java/util/ArrayList
			method.instructions.add(new InsnNode(DUP));
			method.instructions.add(new FieldInsnNode(
					INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V"));
			method.instructions.add(new VarInsnNode(ASTORE, 1));
			// L1
			LabelNode L1 = new LabelNode();
			method.instructions.add(L1);
			method.instructions.add(new FieldInsnNode(
					GETSTATIC, "net/minecraft/client/settings/KeyBinding", "hash",
					"Ljava.util/ArrayList;"));
			method.instructions.add(new FieldInsnNode(INVOKEVIRTUAL,
					"java/util/ArrayList", "iterator", "()Ljava/util/Iterator;"));
			method.instructions.add(new VarInsnNode(ASTORE, 2));
			// L2
			LabelNode L2 = new LabelNode();
			method.instructions.add(L2);
			method.instructions.add(new VarInsnNode(ALOAD, 2));
			method.instructions.add(new FieldInsnNode(INVOKEINTERFACE, "java/util/Iterator",
					"hasNext", "()Z"));
			LabelNode L3 = new LabelNode();
			method.instructions.add(new JumpInsnNode(IFEQ, L3));
			method.instructions.add(new VarInsnNode(ALOAD, 2));
			method.instructions.add(new FieldInsnNode(INVOKEINTERFACE, "java/util/Iterator",
					"next", "()Ljava/lang/Object;"));
			// todo 'CHECKCAST net/minecraft/client/settings/KeyBinding'
			method.instructions.add(new VarInsnNode(ASTORE, 3));
			// L4
			LabelNode L4 = new LabelNode();
			method.instructions.add(L4);
			method.instructions.add(new VarInsnNode(ALOAD, 3));
			method.instructions.add(new FieldInsnNode(INVOKEVIRTUAL,
					"net/minecraft/client/settings/KeyBinding", "getKeyCode", "()I"));
			method.instructions.add(new VarInsnNode(ILOAD, 0));
			LabelNode L5 = new LabelNode();
			method.instructions.add(new JumpInsnNode(IF_ICMPNE, L5));
			// L6
			method.instructions.add(new VarInsnNode(ALOAD, 1));
			method.instructions.add(new VarInsnNode(ALOAD, 3));
			method.instructions.add(new FieldInsnNode(INVOKEVIRTUAL,
					"java/util/ArrayList", "add", "(Ljava/lang/Object;)Z"));
			method.instructions.add(new InsnNode(POP));
			// L5
			method.instructions.add(L5);
			method.instructions.add(new JumpInsnNode(GOTO, L2));
			// L3
			method.instructions.add(L3);
			method.instructions.add(new VarInsnNode(ALOAD, 1));
			method.instructions.add(new InsnNode(ICONST_0));
			// todo ANEWARRAY net/minecraft/client/settings/KeyBinding
			method.instructions.add(new FieldInsnNode(INVOKEVIRTUAL,
					"java/util/ArrayList", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;"));
			// todo CHECKCAST [Lnet/minecraft/client/settings/KeyBinding;
			method.instructions.add(new InsnNode(ARETURN));
			// L7
			LabelNode L7 = new LabelNode();
			method.instructions.add(L7);
			/*
			LOCALVARIABLE keyBinding Lmodwarriors/notenoughkeys/KeyBinding2; L4 L5 3
			LOCALVARIABLE i$ Ljava/util/Iterator; L2 L3 2
			LOCALVARIABLE keyCode I L0 L7 0
			LOCALVARIABLE validKeys Ljava/util/ArrayList; L1 L7 1
			// signature Ljava/util/ArrayList<Lmodwarriors/notenoughkeys/KeyBinding2;>;
			// declaration: java.util.ArrayList<modwarriors.notenoughkeys.KeyBinding2>
			MAXSTACK = 2
			MAXLOCALS = 4
			 */
			method.localVariables.add(new LocalVariableNode("keyBinding",
					"Lnet/minecraft/client/settings/KeyBinding;", null, L4, L5, 3));
			method.localVariables.add(new LocalVariableNode("i$",
					"Ljava/util/Iterator;", null, L2, L3, 2));
			method.localVariables.add(new LocalVariableNode("keyCode",
					"I", null, L0, L7, 0));
			method.localVariables.add(new LocalVariableNode("validKeys",
					"Ljava/util/ArrayList;", null, L1, L7, 1));

			// end method
			classNode.methods.add(method);

			ClassWriter classWriter = new ClassWriter(
					ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		}
		return basicClass;
	}

}
