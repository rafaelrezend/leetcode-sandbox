/**
 * https://leetcode.com/problems/lru-cache/description/
 *
 * Class that represents a Least Recently Used (LRU) cache.
 */
class LRUCache(val capacity: Int) {

    // HashMap containing seach keys with the corresponding cache entries as values
    // It works as an index in order to hit the cache with time complexity O(1)
    val cacheKeyMap = mutableMapOf<Int,CacheEntry>()

    // The actual cache uses doubly linked list, where each element (cache entry) has
    // a pointer to the previous and next elements.
    // In this list, elements are sorted from latest (head) to least (tail) used.

    // The chosen design uses head and tail sentinels nodes in order to simplify the
    // algorithm, as it eliminates the need to special handling of edge nodes.
    // That is, all nodes added or removed from cache are middle nodes and always have
    // a previous and next node.

    // Head sentinel node.
    var headCacheSentinel = CacheEntry(0,0,null,null)
    // Tail sentinel node.
    var tailCacheSentinel = CacheEntry(0,0,null,null)

    // Current cache size
    var cacheSize = 0

    // Constructor. Connects head and tail sentinel nodes to each other.
    init {
        headCacheSentinel.next = tailCacheSentinel
        tailCacheSentinel.previous = headCacheSentinel
    }

    /*
     * Returns value from cache if it exists, -1 otherwise.
     */
    fun get(key: Int): Int {
        // Get cached value by key from hashmap
        val cachedEntry = cacheKeyMap.get(key)
        // If a cached entry is found...
        if (cachedEntry != null) {
            // Remove cache entry from its current position
            removeEntry(cachedEntry)
            // Add it to head
            addToHead(cachedEntry)
            // Return corresponding value
            return cachedEntry.value
        }
        // If cache entry does not exist, return -1
        else return -1
    }

    /*
     * Sets value to cache.
     * If the cache already exists, it gets update. Otherwise, a new cache entry
     * is added and the least recently used removed.
     */
    fun put(key: Int, value: Int) {
        // Get cached value by key from hashmap
        val cachedEntry = cacheKeyMap.get(key)
        // If a cached entry is found...
        if (cachedEntry != null) {
            // Remove cache entry from its current position
            removeEntry(cachedEntry)
            // Add it to head
            addToHead(cachedEntry)
            // Set its new value
            cachedEntry.value = value
        }
        // If cached entry not found...
        else {
            // Create cache entry
            val cacheEntry = CacheEntry(key, value, null, null)
            // Add it to head
            addToHead(cacheEntry)
            // Add it to hashMap for quick search
            cacheKeyMap.put(key, cacheEntry)

            // If cache size exceeds the capacity, remove from hashmap and cache tail
            if (cacheSize > capacity) {
                // Remove cache tail
                val removedEntry = removeEntry(tailCacheSentinel.previous!!)
                // Remove from hashMap
                cacheKeyMap.remove(removedEntry.key)
            }
        }
    }

    /*
     * Removes entry from the cache.
     */
    private fun removeEntry(cachedEntry: CacheEntry) : CacheEntry {
        //// Removes node from its current position.
        // That is, creates a direct doubly link between the previous and the
        // next cache entries of the present cache entry (which will be moved to head).
        // Notice the null assertion (!!). If a node exists, it must be part of the data
        // structure, with previous and next nodes.
        cachedEntry.previous!!.next = cachedEntry.next
        cachedEntry.next!!.previous = cachedEntry.previous

        // Decrement cacheSize because now there is one less link in the List
        cacheSize--

        return cachedEntry
    }

    /*
     * Adds cache entry to the head of the list.
     */
    private fun addToHead(cachedEntry: CacheEntry) {
        // Connect moved node first
        cachedEntry.previous = headCacheSentinel
        cachedEntry.next = headCacheSentinel.next
        // Update the earlier head entry
        headCacheSentinel.next!!.previous = cachedEntry
        // Finally update the head sentinel to the new head
        headCacheSentinel.next = cachedEntry

        // Node added to the head. Increment cache size.
        cacheSize++
    }
}

/* Represents a cache entry in a doubly linkedlist */
class CacheEntry(val key: Int, var value: Int, var previous: CacheEntry?, var next: CacheEntry?)

/**
 * Your LRUCache object will be instantiated and called as such:
 * var obj = LRUCache(capacity)
 * var param_1 = obj.get(key)
 * obj.put(key,value)
 */