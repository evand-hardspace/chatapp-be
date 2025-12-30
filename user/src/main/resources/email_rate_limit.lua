local rateLimitKey = KEYS[1]
local attemptCountKey = KEYS[2]

local backoffSeconds = {}
for i = 3, #KEYS do
    backoffSeconds[i-2] = tonumber(KEYS[i])
end

if redis.call('EXISTS', rateLimitKey) == 1 then
    local ttl = redis.call('TTL', rateLimitKey)
    return { -1, ttl > 0 and ttl or 60 }
end

local currentCount = redis.call('GET', attemptCountKey)
currentCount = currentCount and tonumber(currentCount) or 0

local newCount = redis.call('INCR', attemptCountKey)

local backoffIndex = math.min(currentCount, 2) + 1

redis.call('SETEX', rateLimitKey, backoffSeconds[backoffIndex], '1')
redis.call('EXPIRE', attemptCountKey, 86400)

return { newCount, 0 }