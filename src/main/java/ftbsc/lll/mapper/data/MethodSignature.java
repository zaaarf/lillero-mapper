package ftbsc.lll.mapper.data;

import java.util.Objects;

/**
 * Container class for method signature data.
 */
public class MethodSignature {
	/**
	 * The name of the method.
	 */
	public final String name;

	/**
	 * The descriptor of the method.
	 */
	public final String descriptor;

	/**
	 * Constructs a new {@link MethodSignature}. The parameters should be
	 * either plain or mapped in the same way;
	 * @param name       the method name
	 * @param descriptor the method descriptor
	 */
	public MethodSignature(String name, String descriptor) {
		this.name = name;
		this.descriptor = descriptor;
	}

	/**
	 * Checks if two {@link MethodSignature}s represent the same method.
	 * @param o the other signature
	 * @return whether they represent the same method
	 */
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		MethodSignature signature = (MethodSignature) o;
		return Objects.equals(this.name, signature.name) && Objects.equals(this.descriptor, signature.descriptor);
	}

	/**
	 * Calculates a hash based on name and descriptor.
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.descriptor);
	}
}
