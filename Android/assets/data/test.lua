function onCreate(world)
    ground = luajava.newInstance(Ground)
    ground:addVertex(800, 480)
    ground:addVertex(792, 480)
    ground:addVertex(792, 8)
    ground:addVertex(8, 8)
    ground:addVertex(8, 472)
    ground:addVertex(792, 472)
    ground:addVertex(792, 480)
    ground:addVertex(0, 480)
    ground:addVertex(0, 0)
    ground:addVertex(800, 0)
    world:add(ground)

    for i = 0, 49 do
        for j = 0, 29 do
            drop = luajava.newInstance(Drop)
            world:add(drop)
            drop:setPosition(luajava.newInstance(Vector2, i * 10 + 30, j * 10 + 20))
        end
    end
end

function onCheck(mArray)
    return false
end