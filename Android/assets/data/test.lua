function onCreate(world)
    ground = luajava.newInstance(Ground)
    ground:addVertex(792, 392)
    ground:addVertex(784, 392)
    ground:addVertex(784, 8)
    ground:addVertex(8, 8)
    ground:addVertex(8, 392)
    ground:addVertex(0, 392)
    ground:addVertex(0, 0)
    ground:addVertex(792, 0)
    ground:addVertex(792, 384)
    world:add(ground)

    for i = 0, 49 do
        for j = 0, 29 do
            drop = luajava.newInstance(Drop)
            world:add(drop)
            drop:setPosition(luajava.newInstance(Vector2, i * 15 + 30, j * 15 + 20))
        end
    end
end

function onCheck(mArray)
    return false
end