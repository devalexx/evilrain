dofile "data/main.lua"

function onCreate(world)
    drop = luajava.newInstance(Drop)
    world:add(drop)
    drop:setPosition(luajava.newInstance(Vector2, 100, 400))

    for i = 0, 9 do
        for j = 0, 9 do
            drop = luajava.newInstance(Drop)
            world:add(drop)
            drop:setPosition(luajava.newInstance(Vector2, i * 15 + 130, j * 15 + 140))
        end
    end

    ground = luajava.newInstance(Ground)
    ground:addVertex(752, 48)
    ground:addVertex(752, 344)
    ground:addVertex(696, 208)
    ground:addVertex(600, 208)
    ground:addVertex(552, 344)
    ground:addVertex(504, 136)
    ground:addVertex(272, 152)
    ground:addVertex(96, 136)
    ground:addVertex(32, 360)
    ground:addVertex(32, 32)
    world:add(ground)
end

function onCheck(mArray)
    for i = 0, mArray:size() - 1 do
        if mArray:get(i):getStringType() == "DROP" then
            if mArray:get(i):getPosition().x < 50 then
                return true
            end
        end
    end

    return false
end