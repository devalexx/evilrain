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
    ground:addVertex(736, 48)
    ground:addVertex(736, 352)
    ground:addVertex(720, 352)
    ground:addVertex(712, 320)
    ground:addVertex(688, 296)
    ground:addVertex(656, 288)
    ground:addVertex(600, 272)
    ground:addVertex(568, 280)
    ground:addVertex(544, 288)
    ground:addVertex(512, 296)
    ground:addVertex(472, 280)
    ground:addVertex(440, 248)
    ground:addVertex(408, 192)
    ground:addVertex(392, 128)
    ground:addVertex(376, 72)
    ground:addVertex(232, 72)
    ground:addVertex(232, 184)
    ground:addVertex(200, 184)
    ground:addVertex(200, 72)
    ground:addVertex(184, 72)
    ground:addVertex(184, 120)
    ground:addVertex(160, 120)
    ground:addVertex(160, 72)
    ground:addVertex(136, 72)
    ground:addVertex(136, 160)
    ground:addVertex(96, 160)
    ground:addVertex(96, 72)
    ground:addVertex(72, 72)
    ground:addVertex(56, 168)
    ground:addVertex(40, 288)
    ground:addVertex(16, 344)
    ground:addVertex(8, 368)
    ground:addVertex(8, 48)
    world:add(ground)
end

function onCheck(mArray)
    mArray:size()
    for i = 0, mArray:size() - 1 do
        if mArray:get(i):getStringType() == "DROP" then
            if mArray:get(i):getPosition().x < 100 then
                return true
            end
        end
    end

    return false
end