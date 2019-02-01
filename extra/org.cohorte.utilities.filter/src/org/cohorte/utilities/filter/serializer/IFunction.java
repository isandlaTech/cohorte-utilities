package org.cohorte.utilities.filter.serializer;

/**
 * description of a function handler . it will be use for translate the field
 * (str) to result type use (e.g jooq)
 * 
 * @author apisu
 *
 * @param <T>
 * @param <R>
 */
public interface IFunction<T, R> {

	public R call(T aFieldValue);

}
