package org.cohorte.utilities.filter.serializer;

public interface IFunction<T, R> {

	public R call(T aFieldValue);

}
