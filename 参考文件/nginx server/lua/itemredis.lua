local args = ngx.req.get_uri_args()
local id = args["id"]
local redis = require "resty.redis"
local cache = redis:new()
local ok,err = cache:connect("172.31.96.80",6379)
local item_model = cache:get("item_"..id)
if item_model == ngx.null or item_model == nil then
    --如果取不到，就请求后端接口
    local resp = ngx.location.capture("/item/get?id="..id)
    --将后端返回的json响应，因为是只读redis slave 负责读，不需要存到缓存
    item_model = resp.body
end

ngx.say(item_model)