interface Brightify {
	Loader load();
	Saver save();
	Deleter delete();
}

interface InitialLoader {
	InitialLoader group(Class<?> loadGroup);
	InitialLoader groups(Class<?>... loadGroups);

	<E> TypedFilterOrderLimitListIterable<E> type(Class<E> entityClass);

	<E> Result<E> key(Key<E> key);
	<E> Result<List<E>> keys(Key<E>... keys);
	<E> Result<List<E>> keys(Collection<Key<E>> keys);
}

interface TypedFilterOrderLimitListIterable<E> extends TypedLoader<E>, FilterLoader<E>, Orderable<E>, Limitable<E>, ListIterable<E>

interface TypedLoader<E> {
	Result<E> id(long id);
	Result<List<E>> ids(Long... ids);
	Result<List<E>> ids(Collection<Long> ids);
}

interface FilterLoader<E> {
	OperatorFilterOrderLimitListIterable<E> filter(String condition, Object... params);
	OperatorFilterOrderLimitListIterable<E> filter(EntityFilter filter);
}

interface OperatorFilterLoader<E> {
	FilterLoader<E> or();
	FilterLoader<E> and():

	OperatorFilterOrderLimitListIterable<E> or(String condition, Object... params);
	OperatorFilterOrderLimitListIterable<E> or(EntityFilter filter);
	OperatorFilterOrderLimitListIterable<E> and(String condition, Object... params);
	OperatorFilterOrderLimitListIterable<E> and(EntityFilter filter);
}

interface OperatorFilterOrderLimitListIterable<E> extends OperatorFilterLoader<E>, Orderable<E>, Limitable<E>, ListIterable<E> { }

interface Orderable<E> {
	OrderDirectionLimitListIterable<E> orderBy(String columnName);
	OrderDirectionLimitListIterable<E> orderBy(Property columnProperty);
}

interface OrderLimitListIterable<E> extends Orderable<E>, Limitable<E>, ListIterable<E> { }

interface OrderDirectionSelector<E> {
	OrderLimitListIterable<E> desc();
}

interface OrderDirectionLimitListIterable<E> extends Orderable<E>, OrderDirectionSelector<E>, Limitable<E>, ListIterable<E> { }

interface Limitable<E> {
	OffsetListIterable limit(int limit);
}

interface Offsetable<E> {
	ListIterable<E> offset(int offset);
}

interface OffsetListIterable<E> extends OffsetListIterable<E>, ListIterable<E> { } 


interface ListIterable<E> extends Iterable<E> {
	List<E> list();
}

class EntityFilter {
	
	EntityFilter or(String condition, Object... params);
	EntityFilter or(EntitfyFilter filter);

	EntityFilter and(String condition, Object... params);
	EntityFilter and(EntityFilter filter);

	static EntityFilter filter(String condition, Object... params);
	static EntityFilter filter(EntityFilter filter);
}

interface Saver {
	<E> Result<E> entity(E entity);
	<E> Result<Map<Key<E>, E>> entities(E... entities);
	<E> Result<Map<Key<E>, E>> entities(Collection<E> entities);
}


interface Deleter {
	<E> Result<E> entity(E entity);
	<E> Result<Map<E, Boolean> entities(E... entities);
	<E> Result<Map<E, Boolean> entities(Collection<E> entities);
	
	<E> Result<Boolean> key(Key<E> key);
	<E> Result<Map<Key<E>>, Boolean> keys(Key<E>... keys);
	<E> Result<Map<Key<E>>, Boolean> keys(Collection<Key<E> keys);

	<E> TypedDeleter<E> type(Class<E> entityClass);
}

interface TypedDeleter<E> {
	Result<Boolean> id(long id);
	Result<Map<Key<E>, Boolean>> ids(Long... ids);
	Result<Map<Key<E>, Boolean>> ids(Collection<Long> ids);
}

interface Result<E> {
	E now();
	void async(Callback<E> callback);
}