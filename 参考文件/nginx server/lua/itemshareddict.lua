--取缓存
function get_from_cache(key)
    --类似于拿到缓存对象
    local cache_ngx = ngx.shared.my_cache
    --从缓存对象中，根据key获得值
    local value = cache_ngx:get(key)
    return value
end

--存缓存
function set_to_cache(key,value,exptime)
    if not exptime then 
        exptime = 0
    end
    local cache_ngx = ngx.shared.my_cache
    local succ,err,forcible = cache_ngx:set(key,value,exptime)
    return succ 
end

--编写“main"函数：


--得到请求的参数，类似Servlet的request.getParameters
local args = ngx.req.get_uri_args()
local id = args["id"]
--从缓存里面获取商品信息
local item_model = get_from_cache("item_"..id)
if item_model == nil then
    --如果取不到，就请求后端接口
    local resp = ngx.location.capture("/item/get?id="..id)
    --将后端返回的json响应，存到缓存里面
    item_model = resp.body
    set_to_cache("item_"..id,item_model,1*60)
end
ngx.say(item_model)